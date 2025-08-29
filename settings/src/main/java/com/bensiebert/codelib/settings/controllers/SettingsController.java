package com.bensiebert.codelib.settings.controllers;

import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.primitive.Auth;
import com.bensiebert.codelib.settings.data.Setting;
import com.bensiebert.codelib.settings.data.SettingRepository;
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

    @RequestMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Object getSettings(@RequestHeader(name = "Authorization") String authHeader) {
        User user = Auth.getUserByHeader(authHeader);

        if (user == null) return Map.of("error", "Invalid or missing authentication token.");

        List<Setting> settings = repo.getSettingsByUser(user);

        for(Setting setting : settings) {
            setting.setUser(null);
        }

        return settings;
    }

    @RequestMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Object createOrUpdateSetting(@RequestHeader(name = "Authorization") String authHeader, @RequestBody PostBody body) {
        User user = Auth.getUserByHeader(authHeader);

        if (user == null) return Map.of("error", "Invalid or missing authentication token.");

        Setting existing = repo.getSettingsByKeyAndUser(body.getKey(), user);

        if(existing != null) {
            existing.setValue(body.getValue());
            existing = repo.save(existing);
            return existing.withUser(null);
        }

        Setting setting = new Setting();
        setting.setUser(user);
        setting.setKey(body.getKey());
        setting.setValue(body.getValue());
        setting = repo.save(setting);
        setting = setting.withUser(null);

        return setting;
    }

    @RequestMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public Object deleteSetting(@RequestHeader(name = "Authorization") String authHeader, @RequestBody DeleteBody body) {
        User user = Auth.getUserByHeader(authHeader);

        if (user == null) return Map.of("error", "Invalid or missing authentication token.");

        Setting existing = repo.getSettingsByKeyAndUser(body.getKey(), user);

        if(existing != null) {
            repo.delete(existing);
        }

        return Map.of("status", "ok");
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
}
