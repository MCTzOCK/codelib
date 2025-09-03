package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.hooks.AuthHooks;
import com.bensiebert.codelib.common.crypto.Hashes;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Registser a user account", tags = "users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests")
    })
    @RateLimited(limit = 5, interval = 60)
    @RequestMapping(path = "/auth/register", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object delete(@RequestBody ReqBody body) {
        if(body == null) return Map.of("error", "Invalid request body.");

        if(users.existsByUsername(body.getUsername())) {
            return Map.of("error", "Username is already in use.");
        }

        if(users.existsByEmail(body.getEmail())) {
            return Map.of("error", "Email is already in use.");
        }

        if(body.getPassword().length() < 8) {
            return Map.of("error", "Password must be at least 8 characters long.");
        }

        if(body.getUsername().length() < 3) {
            return Map.of("error", "Username must be at least 3 characters long.");
        }

        if(body.getName().length() < 3) {
            return Map.of("error", "Name must be at least 3 characters long.");
        }

        if(!body.getEmail().contains("@") || !body.getEmail().contains(".")) {
            return Map.of("error", "Invalid email address.");
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
    public static class ReqBody {
        public String username;
        public String password;
        public String name;
        public String email;
    }
}
