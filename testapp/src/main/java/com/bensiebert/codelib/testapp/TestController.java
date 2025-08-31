package com.bensiebert.codelib.testapp;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.auth.annotations.CurrentUser;
import com.bensiebert.codelib.auth.data.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class TestController {

    @Authenticated(customMethod = "checkAccessTest", roles = {"user", "admin"})
    @RequestMapping(path = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testEndpoint() {
        return new HashMap<>() {{
            put("status", "ok");
            put("message", "Test endpoint is working!");
        }};
    }

    public boolean checkAccessTest(User user) {
        return user != null && user.getUsername().equals("ben");
    }

    @Authenticated(roles = {"admin"})
    @GetMapping(path = "/test2", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object testEndpoint2(@CurrentUser User user) {
        return new HashMap<>() {{
            put("status", "ok");
            put("message", "Test endpoint 2 is working and you are an admin!");
            put("user", user);
        }};
    }
}
