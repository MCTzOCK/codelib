package com.bensiebert.codelib.settings.data;

import com.bensiebert.codelib.auth.data.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {
    List<Setting> getSettingsByUser(User user);

    Setting getSettingsByKeyAndUser(String key, User user);
}