package com.bensiebert.codelib.testapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.bensiebert.codelib.testapp",
                "com.bensiebert.codelib.auth",
                "com.bensiebert.codelib.admin",
                "com.bensiebert.codelib.settings"
        }
)
public class TestApp {

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
