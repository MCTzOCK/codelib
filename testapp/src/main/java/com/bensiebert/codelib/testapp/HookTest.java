package com.bensiebert.codelib.testapp;


import com.bensiebert.codelib.admin.hooks.AdminSQLExecuted;
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

    @AdminSQLExecuted
    public void onAdminSQLExecuted(User user, String sql) {
        System.out.println("Admin executed SQL: " + sql + " by user: " + user.getName());
    }

    @AdminSQLExecuted
    public void onAdminSQLExecuted2(User user, String sql) {
        System.out.println("Admin executed SQL (2): " + sql + " by user: " + user.getName());
    }
}
