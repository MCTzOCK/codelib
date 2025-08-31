package com.bensiebert.codelib.faq.hooks;

import com.bensiebert.codelib.hooks.Hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Hook("faq.deleted")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FAQDeleted {
}
