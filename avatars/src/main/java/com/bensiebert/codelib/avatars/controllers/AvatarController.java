package com.bensiebert.codelib.avatars.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.springdoc.BasicErrorResponse;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.avatars.data.Avatar;
import com.bensiebert.codelib.avatars.data.AvatarRepository;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import com.bensiebert.codelib.ratelimiting.springdoc.Error429Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AvatarController {

    @Autowired
    private AvatarRepository repo;

    @Operation(summary = "Update an avatar", tags = {"User avatars"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Avatar200Response.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BasicErrorResponse.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))}
            ),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))}
            )
    })
    @RateLimited(limit = 5, interval = 60)
    @Authenticated(roles = {"user"})
    @RequestMapping(path = "/avatars", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateAvatar(@RequestParam(name = "url") String url, @Parameter(hidden = true) @CurrentUser User user, HttpServletResponse response) {
        if(url == null || !url.startsWith("http") || !url.contains("://")) {
            return reject("Invalid URL", null);
        }

        Avatar a = repo.getAvatarByUserId(user.getId());

        if(a == null) {
            a = new Avatar();
            a.setUser(user);
        }

        a.setAvatar(url);

        repo.save(a);

        HookManager.fire("avatar.updated", user, url);


        return new Avatar200Response();
    }

    @Operation(summary = "Get a user's avatar by their user ID", tags = {"User avatars"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to avatar URL"),
            @ApiResponse(responseCode = "404", description = "Avatar not found")
    })
    @RequestMapping(path = "/avatars/{id}", method = RequestMethod.GET)
    public void getAvatar(@PathVariable(name = "id") String id, HttpServletResponse response) {
        Avatar a = repo.getAvatarByUserId(id);
        if(a == null || a.getAvatar() == null || a.getAvatar().isEmpty()) {
            response.setStatus(404);
            return;
        }

        response.setStatus(302);
        response.setHeader("Location", a.getAvatar());
    }

    public Object reject(String message, HttpServletResponse res) {
        res.setStatus(400);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            res.getWriter().write("{\"error\": \"bad request\", \"message\": \"" + message + "\"}");
            res.getWriter().flush();
        } catch (Exception ignored) {}
        return null;
    }

    public static class Avatar200Response {
        public String status = "ok";
        public String message = "Avatar updated successfully.";
    }

}
