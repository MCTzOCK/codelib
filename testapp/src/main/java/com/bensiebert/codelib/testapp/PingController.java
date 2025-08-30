package com.bensiebert.codelib.testapp;

import com.bensiebert.codelib.ratelimiting.RateLimited;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @RateLimited(limit = 3, interval = 10)
    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object ping() {
        return new Object() {
            public final String response = "pong";
        };
    }
}
