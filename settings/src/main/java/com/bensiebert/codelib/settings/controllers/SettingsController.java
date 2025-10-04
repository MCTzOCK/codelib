package com.bensiebert.codelib.settings.controllers;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.data.UserRepository;
import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import com.bensiebert.codelib.ratelimiting.springdoc.Error429Response;
import com.bensiebert.codelib.settings.data.Setting;
import com.bensiebert.codelib.settings.data.SettingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@ConditionalOnBooleanProperty(
        prefix = "codelib.settings",
        name = "enable",
        havingValue = true,
        matchIfMissing = true
)
public class SettingsController {

    @Autowired
    private SettingRepository repo;

    @Autowired
    private UserRepository users;

    @Operation(summary = "Get all settings for the authenticated user", tags = {"Settings"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Setting.class)
                    ))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @Authenticated
    @RateLimited(limit = 1, interval = 1)
    @RequestMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Object getSettings(@Parameter(hidden = true) @CurrentUser User user) {
        List<Setting> settings = repo.getSettingsByUser(getFromDetached(user));

        for (Setting setting : settings) {
            setting.setUser(null);
        }

        return settings;
    }

    @Operation(summary = "Create or update a setting for the authenticated user", tags = {"Settings"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting created / updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Setting.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @Authenticated
    @RateLimited(limit = 10, interval = 10)
    @RequestMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object createOrUpdateSetting(@Parameter(hidden = true) @CurrentUser User user, @RequestBody PostBody body) {
        if (user == null) return Map.of("error", "Invalid or missing authentication token.");

        Setting existing = repo.getSettingsByKeyAndUser(body.getKey(), getFromDetached(user));

        if (existing != null) {
            existing.setValue(body.getValue());
            existing = repo.save(existing);
            HookManager.fire("settings.updated", existing, user);
            return existing.withUser(null);
        }

        Setting setting = new Setting();
        setting.setUser(getFromDetached(user));
        setting.setKey(body.getKey());
        setting.setValue(body.getValue());
        setting = repo.save(setting);
        setting = setting.withUser(null);
        HookManager.fire("settings.created", setting, user);

        return setting;
    }

    @Operation(summary = "Delete a setting for the authenticated user", tags = {"Settings"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting created / updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsDeletedResponse200.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @Authenticated
    @RateLimited(limit = 1, interval = 1)
    @RequestMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public Object deleteSetting(@Parameter(hidden = true) @CurrentUser User user, @RequestBody DeleteBody body) {
        Setting existing = repo.getSettingsByKeyAndUser(body.getKey(), getFromDetached(user));

        if (existing != null) {
            repo.delete(existing);
            HookManager.fire("settings.deleted", existing, user);
        }

        return new SettingsDeletedResponse200("OK");
    }

    @Getter
    public static class PostBody {
        public String key;
        public String value;
    }

    @Getter
    public static class DeleteBody {
        public String key;
    }

    @AllArgsConstructor
    public static class SettingsDeletedResponse200 {
        public String status;
    }

    public User getFromDetached(User user) {
        return users.findById(user.getId()).orElse(null);
    }
}
