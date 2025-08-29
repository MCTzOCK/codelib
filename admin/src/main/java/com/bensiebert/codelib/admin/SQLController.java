package com.bensiebert.codelib.admin;

import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.primitive.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SQLController {

    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping(path = "/admin/database/sql", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object sql(@RequestHeader(name = "Authorization", required = false) String authHeader, @RequestBody String sql) {
        User user = Authentication.getUserByHeader(authHeader);

        if(!Authentication.isAdmin(user)) {
            return Map.of("error", "Unauthorized");
        }

        return jdbc.queryForList(sql);
    }

    @PostMapping(path = "/admin/database/dml", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object dml(@RequestHeader(name = "Authorization") String authHeader, @RequestBody String sql) {
        User user = Authentication.getUserByHeader(authHeader);

        if(!Authentication.isAdmin(user)) {
            return Map.of("error", "Unauthorized");
        }

        jdbc.execute(sql);

        return Map.of("status", "OK");
    }

}
