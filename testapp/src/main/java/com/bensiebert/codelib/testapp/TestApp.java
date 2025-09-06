package com.bensiebert.codelib.testapp;

import com.bensiebert.codelib.hooks.HookManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "com.bensiebert.codelib.testapp",
                "com.bensiebert.codelib.hooks",
                "com.bensiebert.codelib.auth",
                "com.bensiebert.codelib.admin",
                "com.bensiebert.codelib.settings",
                "com.bensiebert.codelib.ratelimiting",
                "com.bensiebert.codelib.crud",
                "com.bensiebert.codelib.faq",
                "com.bensiebert.codelib.onboarding",
                "com.bensiebert.codelib.avatars",
                "com.bensiebert.codelib.userdiscovery",
        }
)
@EnableJpaRepositories("com.bensiebert.codelib.testapp")
@EntityScan("com.bensiebert.codelib.testapp")
public class TestApp {

    public static void main(String[] args) {
        HookManager.scan("com.bensiebert.codelib.hooks",
                "com.bensiebert.codelib.auth",
                "com.bensiebert.codelib.admin",
                "com.bensiebert.codelib.ratelimiting",
                "com.bensiebert.codelib.testapp",
                "com.bensiebert.codelib.settings",
                "com.bensiebert.codelib.faq",
                "com.bensiebert.codelib.onboarding",
                "com.bensiebert.codelib.avatars",
                "com.bensiebert.codelib.userdiscovery"
        );
        SpringApplication.run(TestApp.class, args);
    }
}
