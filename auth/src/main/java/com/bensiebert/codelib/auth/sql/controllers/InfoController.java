package com.bensiebert.codelib.auth.sql.controllers;

import com.bensiebert.codelib.auth.primitive.Authentication;
import com.bensiebert.codelib.auth.sql.data.User;
import com.bensiebert.codelib.auth.sql.data.UserRepository;
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
        name = "info",
        havingValue = true,
        matchIfMissing = true
)
public class InfoController {

    @RequestMapping(path = "/auth/info", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Object delete(@RequestHeader(name = "Authorization") String authHeader) {
        User user = Authentication.getUserByHeader(authHeader);

        if(user == null) {
            return new Object() {
                public final String status = "error";
                public final String message = "Invalid or missing authentication token.";
            };
        }

        return user.withPasswordHash("");
    }
}
