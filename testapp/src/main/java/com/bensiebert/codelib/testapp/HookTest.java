package com.bensiebert.codelib.testapp;


import com.bensiebert.codelib.auth.data.User;
import com.bensiebert.codelib.auth.hooks.UserCreated;
import com.bensiebert.codelib.auth.hooks.UserLoggedIn;

public class HookTest {


    @UserCreated
    public void onUserCreated(User user) {
        System.out.println("User created: " + user.getName());
    }

    @UserLoggedIn
    public void onUserLoggedIn(User user) {
        System.out.println("User logged in: " + user.getName());
    }

}
