package com.bensiebert.codelib.ratelimiting;

import com.bensiebert.codelib.hooks.HookManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Aspect
public class RateLimitedAspect {

    private final ConcurrentHashMap<String, RateLimitEntry> map = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimited)")
    public Object around(ProceedingJoinPoint pjp, RateLimited rateLimited) throws Throwable {
        String key = pjp.getSignature().toShortString();
        long now = Instant.now().toEpochMilli() / 1000;

        RateLimitEntry entry = map.computeIfAbsent(key, k -> new RateLimitEntry(now));

        boolean allowed = entry.allow(rateLimited.limit(), rateLimited.interval(), now);

        if (allowed) {
            return pjp.proceed();
        }

        HookManager.fire("ratelimiting.after_rate_limited", pjp, rateLimited);
        throw new RateLimitExceededException(
                rateLimited.limit() + "," + rateLimited.interval()
        );
    }
}
