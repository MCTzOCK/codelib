package com.bensiebert.codelib.todo.hooks;

import com.bensiebert.codelib.hooks.Hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Hook("todo.list.updated")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TodoListUpdated {
}
