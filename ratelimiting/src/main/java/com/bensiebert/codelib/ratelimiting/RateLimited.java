package com.bensiebert.codelib.ratelimiting;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {
    int limit() default 5;
    int interval() default 10;
}
