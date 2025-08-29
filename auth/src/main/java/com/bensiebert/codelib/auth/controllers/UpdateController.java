package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.primitive.Auth;
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
        name = "enable-update",
        havingValue = true,
        matchIfMissing = true
)
public class UpdateController {

    @Autowired
    private UserRepository users;

    @RequestMapping(path = "/auth/update", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object update(@RequestHeader(name="Authorization") String authHeader, @RequestBody ReqBody body) {
        User user = Auth.getUserByHeader(authHeader);
        if(user == null) return Map.of("error", "Invalid or missing authentication token.");

        if(body.getEmail() != null && !body.getEmail().isEmpty()) {
            if(users.existsByEmail(body.getEmail())) {
                return Map.of("error", "Email is already in use.");
            }
            user = user.withEmail(body.getEmail());
        }

        if(body.getPassword() != null && !body.getPassword().isEmpty()) {
            if(body.getPassword().length() < 8) {
                return Map.of("error", "Password must be at least 8 characters long.");
            }
            user = user.withPasswordHash(Hashes.sha256(body.getPassword()));
        }

        if(body.getName() != null && !body.getName().isEmpty()) {
            if(body.getName().length() < 3) {
                return Map.of("error", "Name must be at least 3 characters long.");
            }
            user = user.withName(body.getName());
        }

        users.save(user);

        return user.withPasswordHash("");
    }

    @Getter
    public static class ReqBody {
        public String name;
        public String email;
        public String password;
    }
}
