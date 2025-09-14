package com.bensiebert.codelib.auth.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.hooks.AuthHooks;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.springdoc.DeleteResponse200;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import com.bensiebert.codelib.ratelimiting.springdoc.Error429Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "codelib.auth",
        name = "enable-delete",
        havingValue = true,
        matchIfMissing = true
)
public class DeleteController {

    @Autowired
    public UserRepository users;

    @Operation(summary = "Delete a user account", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account deleted successfully",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteResponse200.class))
                    }
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
                    }
            ),
            @ApiResponse(responseCode = "429", description = "Too Many Requests",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
                    })
    })
    @Authenticated
    @RateLimited
    @RequestMapping(path = "/auth/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public Object delete(@Parameter(hidden = true) @CurrentUser User user) {
        users.delete(user);

        HookManager.fire(AuthHooks.USER_DELETED, user);


        return new DeleteResponse200("ok", "User deleted successfully");
    }
}
