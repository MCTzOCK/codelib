package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.data.Token;
import com.bensiebert.codelib.auth.data.TokenRepository;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.hooks.AuthHooks;
import com.bensiebert.codelib.auth.springdoc.LoginResponse200;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.common.crypto.Hashes;
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
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "codelib.auth",
        name = "enable-login",
        havingValue = true,
        matchIfMissing = true
)
public class LoginController {

    @Autowired
    private UserRepository users;

    @Autowired
    private TokenRepository tokens;


    @Operation(summary = "Login to a user account", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse200.class))
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
    @RateLimited
    @RequestMapping(path = "/auth/login", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object delete(@Parameter(schema = @Schema(implementation = LoginController.LoginReqBody.class)) @RequestBody LoginController.LoginReqBody body, HttpServletResponse response) throws Exception {
        if (body == null) return reject(response, "Invalid request body.");

        User user = users.findByUsername(body.getUsername());

        if (user == null) {
            return reject(response, "Invalid username or password.");
        }

        if (user.getPasswordHash().equals(Hashes.sha256(body.getPassword()))) {
            Token tkn = new Token();
            tkn.setUser(user);
            tkn.setExpiryTime(System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 30L);
            tkn = tokens.save(tkn);

            HookManager.fire(AuthHooks.USER_LOGGED_IN, user);
            return Map.of(
                    "token", tkn.getId()
            );
        }

        return Map.of("error", "Invalid username or password.");
    }

    public Object reject(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"unauthorized\", \"message\": \"" + message + "\"}");
        response.getWriter().flush();
        return null;
    }

    @Getter
    public static class LoginReqBody {
        public String username;
        public String password;
    }
}
