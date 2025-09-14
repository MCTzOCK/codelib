package com.bensiebert.codelib.admin;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.primitive.Auth;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.hooks.HookManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Execute DML SQL statements (INSERT, UPDATE, DELETE). Admins only.", tags = "Admin DB Operations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DML executed successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DMLResponse200.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))}
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error (e.g. SQL Error)")
    })
    @Authenticated(roles = {"admin"})
    @PostMapping(path = "/admin/database/dml", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
    public Object dml(@Parameter(description = "DML statement") @RequestBody String sql, @Parameter(hidden = true) @CurrentUser User user) {
        jdbc.execute(sql);

        HookManager.fire("admin.dml_executed", user, sql);

        return new DMLResponse200();
    }

    public static class DMLResponse200 {
        public String status = "OK";
    }
}
