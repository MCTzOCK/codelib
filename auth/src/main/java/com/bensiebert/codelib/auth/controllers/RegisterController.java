package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.hooks.AuthHooks;
import com.bensiebert.codelib.auth.springdoc.BasicErrorResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "codelib.auth",
        name = "enable-register",
        havingValue = true,
        matchIfMissing = true
)
public class RegisterController {

    @Autowired
    private UserRepository users;

    @Operation(summary = "Register a user account", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
                    }),
            @ApiResponse(responseCode = "429", description = "Too Many Requests",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = BasicErrorResponse.class))
                    }),
    })
    @RateLimited(limit = 5, interval = 60)
    @RequestMapping(path = "/auth/register", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object delete(@Parameter(schema = @Schema(implementation = RegisterController.RegisterReqBody.class)) @RequestBody RegisterReqBody body, HttpServletResponse res) {
        if (body == null) return reject("Invalid request body.", res);

        if (users.existsByUsername(body.getUsername())) {
            return reject("Username is already in use.", res);
        }

        if (users.existsByEmail(body.getEmail())) {
            return reject("Email is already in use.", res);
        }

        if (body.getPassword().length() < 8) {
            return reject("Password must be at least 8 characters long.", res);
        }

        if (body.getUsername().length() < 3) {
            return reject("Username must be at least 3 characters long.", res);
        }

        if (body.getName().length() < 3) {
            return reject("Name must be at least 3 characters long.", res);
        }

        if (!body.getEmail().contains("@") || !body.getEmail().contains(".")) {
            return reject("Email is not valid.", res);
        }

        User user = new User();
        user.setEmail(body.getEmail());
        user.setRole("USER");
        user.setName(body.getName());
        user.setUsername(body.getUsername());
        user.setPasswordHash(Hashes.sha256(body.getPassword()));

        user = users.save(user);

        HookManager.fire(AuthHooks.USER_CREATED, user);

        return user.withPasswordHash("");
    }

    @Getter
    public static class RegisterReqBody {
        public String username;
        public String password;
        public String name;
        public String email;
    }

    public Object reject(String message, HttpServletResponse response) {
        response.setStatus(400);
        response.setContentType("application/json");
        try {
            response.getWriter().write("{\"error\": \"bad request\", \"message\": \"" + message + "\"}");
            response.getWriter().flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
