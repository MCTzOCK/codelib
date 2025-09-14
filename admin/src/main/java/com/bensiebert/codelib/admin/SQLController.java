package com.bensiebert.codelib.admin;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.primitive.Auth;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.hooks.HookManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
        name = "enable-sql",
        havingValue = true,
        matchIfMissing = true
)
public class SQLController {

    @Autowired
    private JdbcTemplate jdbc;

    @Operation(summary = "Execute SQL statements. Admins only.", tags = "Admin DB Operations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SQL executed successfully",
                content = {@Content(mediaType = "application/json", array = @ArraySchema(
                        schema = @Schema(type = "object", additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
                ))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))}
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error (e.g. SQL Error)")
    })
    @Authenticated(roles = {"admin"})
    @PostMapping(path = "/admin/database/sql", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
    public Object sql(@Parameter(description = "SQL query") @RequestBody String sql, @Parameter(hidden = true) @CurrentUser User user) {
        HookManager.fire("admin.sql_executed", user, sql);

        return jdbc.queryForList(sql);
    }
}
