package com.bensiebert.codelib.admin;

import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.primitive.Auth;
import com.bensiebert.codelib.hooks.HookManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "codelib.admin",
        name = "enable-sql",
        havingValue = true,
        matchIfMissing = true
)
public class SQLController {

    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping(path = "/admin/database/sql", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object sql(@RequestHeader(name = "Authorization", required = false) String authHeader, @RequestBody String sql) {
        User user = Auth.getUserByHeader(authHeader);

        if(!Auth.isAdmin(user)) {
            return Map.of("error", "Unauthorized");
        }

        HookManager.fire("admin.sql_executed", user, sql);

        return jdbc.queryForList(sql);
    }
}
