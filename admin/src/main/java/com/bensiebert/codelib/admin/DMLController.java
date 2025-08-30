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
        name = "enable-dml",
        havingValue = true,
        matchIfMissing = true
)
public class DMLController {

    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping(path = "/admin/database/dml", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object dml(@RequestHeader(name = "Authorization") String authHeader, @RequestBody String sql) {
        User user = Auth.getUserByHeader(authHeader);

        if(!Auth.isAdmin(user)) {
            return Map.of("error", "Unauthorized");
        }

        jdbc.execute(sql);

        HookManager.fire("admin.dml_executed", user, sql);

        return Map.of("status", "OK");
    }
}
