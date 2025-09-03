package com.bensiebert.codelib.admin;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.primitive.Auth;
import com.bensiebert.codelib.hooks.HookManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Execute DML SQL statements (INSERT, UPDATE, DELETE). Admins only.", tags = "admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DML executed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid SQL")
    })
    @Authenticated(roles = {"admin"})
    @PostMapping(path = "/admin/database/dml", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object dml(@RequestBody String sql, @CurrentUser User user) {
        jdbc.execute(sql);

        HookManager.fire("admin.dml_executed", user, sql);

        return Map.of("status", "OK");
    }
}
