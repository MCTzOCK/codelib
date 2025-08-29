package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.primitive.Authentication;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "auth.routes",
        name = "delete",
        havingValue = true,
        matchIfMissing = true
)
public class DeleteController {

    @Autowired
    public UserRepository users;


    @RequestMapping(path = "/auth/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public Object delete(@RequestHeader(name = "Authorization") String authHeader) {
        User user = Authentication.getUserByHeader(authHeader);

        if(user == null) {
            return new Object() {
                public final String status = "error";
                public final String message = "Invalid or missing authentication token.";
            };
        }

        users.delete(user);

        return new Object() {
            public final String status = "ok";
            public final String message = "User account deleted successfully.";
        };
    }
}
