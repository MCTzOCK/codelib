package com.bensiebert.codelib.testapp;

import com.bensiebert.codelib.hooks.HookManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.bensiebert.codelib.testapp",
                "com.bensiebert.codelib.hooks",
                "com.bensiebert.codelib.auth",
                "com.bensiebert.codelib.admin",
                "com.bensiebert.codelib.settings",
                "com.bensiebert.codelib.ratelimiting"
        }
)
public class TestApp {

    public static void main(String[] args) {
        HookManager.scan(
                "com.bensiebert.codelib.hooks",
                "com.bensiebert.codelib.auth",
                "com.bensiebert.codelib.admin",
                "com.bensiebert.codelib.ratelimiting",
                "com.bensiebert.codelib.testapp"
        );
        SpringApplication.run(TestApp.class, args);
    }
}
