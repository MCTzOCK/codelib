package com.bensiebert.codelib.settings;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = "com.bensiebert.codelib.settings.data")
@EnableJpaRepositories(basePackages = "com.bensiebert.codelib.settings.data")
@EnableTransactionManagement
public class SettingsAutoConfig {
}
