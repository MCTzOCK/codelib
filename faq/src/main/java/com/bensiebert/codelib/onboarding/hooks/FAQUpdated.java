package com.bensiebert.codelib.onboarding.hooks;

import com.bensiebert.codelib.hooks.Hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Hook("faq.updated")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FAQUpdated {
}
