package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.hooks.AuthHooks;
import com.bensiebert.codelib.auth.primitive.Auth;
import com.bensiebert.codelib.auth.springdoc.LoginResponse200;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.common.crypto.Hashes;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import com.bensiebert.codelib.ratelimiting.springdoc.Error429Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "codelib.auth",
        name = "enable-update",
        havingValue = true,
        matchIfMissing = true
)
public class UpdateController {

    @Autowired
    private UserRepository users;

    @Operation(summary = "Update a user account", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
                    }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
                    }),
            @ApiResponse(responseCode = "429", description = "Too Many Requests",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
                    })
    })
    @Authenticated
    @RateLimited(limit = 5, interval = 60)
    @RequestMapping(path = "/auth/update", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object update(@Parameter(hidden = true) @CurrentUser User user,
                         @Parameter(schema = @Schema(implementation = UpdateController.UpdateReqBody.class)) @RequestBody UpdateReqBody body,
                         HttpServletResponse response) {

        if (body.getEmail() != null && !body.getEmail().isEmpty()) {
            if (users.existsByEmail(body.getEmail())) {
                return reject(response, "Email is already in use.");
            }
            user = user.withEmail(body.getEmail());
        }

        if (body.getPassword() != null && !body.getPassword().isEmpty()) {
            if (body.getPassword().length() < 8) {
                return reject(response, "Password must be at least 8 characters long.");
            }
            user = user.withPasswordHash(Hashes.sha256(body.getPassword()));
        }

        if (body.getName() != null && !body.getName().isEmpty()) {
            if (body.getName().length() < 3) {
                return reject(response, "Name must be at least 3 characters long.");
            }
            user = user.withName(body.getName());
        }

        users.save(user);

        HookManager.fire(AuthHooks.USER_UPDATED, user);

        return user.withPasswordHash("");
    }

    @Getter
    public static class UpdateReqBody {
        public String name;
        public String email;
        public String password;
    }

    public Object reject(HttpServletResponse response, String message) {
        response.setStatus(400);
        response.setContentType("application/json");
        try {
            response.getWriter().write("{\"error\": \"bad request\", \"message\": \"" + message + "\"}");
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
