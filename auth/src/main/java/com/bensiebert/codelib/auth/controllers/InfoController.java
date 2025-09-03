package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.primitive.Auth;
import com.bensiebert.codelib.auth.data.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "codelib.auth",
        name = "enable-info",
        havingValue = true,
        matchIfMissing = true
)
public class InfoController {

    @Operation(summary = "Get info about the current account", tags = "users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User info retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @Authenticated
    @RequestMapping(path = "/auth/info", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Object delete(@CurrentUser User user) {
        return user.withPasswordHash("");
    }
}
