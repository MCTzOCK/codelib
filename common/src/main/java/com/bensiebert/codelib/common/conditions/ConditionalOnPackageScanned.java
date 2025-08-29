package com.bensiebert.codelib.common.conditions;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnPackageScannedCondition.class)
public @interface ConditionalOnPackageScanned {
    /**
     * The package name to check if it has been scanned.
     * @return the package name
     */
    String packageName();
}
