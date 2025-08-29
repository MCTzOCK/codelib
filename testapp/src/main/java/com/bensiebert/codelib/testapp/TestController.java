package com.bensiebert.codelib.testapp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class TestController {

    @RequestMapping(path = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testEndpoint() {
        return new HashMap<>() {{
            put("status", "ok");
            put("message", "Test endpoint is working!");
        }};
    }

}
