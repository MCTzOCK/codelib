package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.data.Token;
import com.bensiebert.codelib.auth.data.TokenRepository;
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


    @Operation(summary = "Login to a user account", tags = "users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests")
    })
    @RateLimited
    @RequestMapping(path = "/auth/login", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object delete(@RequestBody ReqBody body) {
        if(body == null) return Map.of("error", "Invalid request body.");

        User user = users.findByUsername(body.getUsername());

        if(user == null) return Map.of("error", "Invalid username or password.");

        if(user.getPasswordHash().equals(Hashes.sha256(body.getPassword()))) {
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

    @Getter
    public static class ReqBody {
        public String username;
        public String password;
    }
}
