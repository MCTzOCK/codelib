package com.bensiebert.codelib.auth.hooks;

import com.bensiebert.codelib.hooks.Hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Hook(AuthHooks.USER_LOGGED_IN)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLoggedIn {
}
