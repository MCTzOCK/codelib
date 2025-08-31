package com.bensiebert.codelib.admin.hooks;

import com.bensiebert.codelib.hooks.Hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This hook is called after the /admin/database/sql endpoint has queried the database.
 */
@Hook("admin.sql_executed")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AdminSQLExecuted {
}
