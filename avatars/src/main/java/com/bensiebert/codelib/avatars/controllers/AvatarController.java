package com.bensiebert.codelib.avatars.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.avatars.data.Avatar;
import com.bensiebert.codelib.avatars.data.AvatarRepository;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Update an avatar", tags = {"Avatars"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid URL"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @RateLimited(limit = 5, interval = 60)
    @Authenticated(roles = {"user"})
    @RequestMapping(path = "/avatars", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateAvatar(@RequestParam(name = "url") String url, @CurrentUser User user) {
        if(url == null || url.isEmpty() || !url.startsWith("http") || !url.contains("://")) {
            return new Object() {
                public final String status = "error";
                public final String message = "URL is not valid.";
            };
        }

        Avatar a = repo.getAvatarByUserId(user.getId());

        if(a == null) {
            a = new Avatar();
            a.setUserId(user.getId());
        }

        a.setAvatar(url);

        repo.save(a);

        HookManager.fire("avatar.updated", user, url);


        return new Object() {
            public final String status = "ok";
            public final String message = "Avatar updated successfully.";
        };
    }

    @Operation(summary = "Get a user's avatar by their user ID")
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

}
