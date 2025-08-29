package com.bensiebert.codelib.auth.sql.controllers;

import com.bensiebert.codelib.auth.sql.data.User;
import com.bensiebert.codelib.auth.sql.data.UserRepository;
import com.bensiebert.codelib.common.crypto.Hashes;
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
        prefix = "auth.routes",
        name = "register",
        havingValue = true,
        matchIfMissing = true
)
public class RegisterController {

    @Autowired
    private UserRepository users;

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
