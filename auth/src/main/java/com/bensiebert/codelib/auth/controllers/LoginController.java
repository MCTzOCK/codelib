package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.data.Token;
import com.bensiebert.codelib.auth.data.TokenRepository;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.common.crypto.Hashes;
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
