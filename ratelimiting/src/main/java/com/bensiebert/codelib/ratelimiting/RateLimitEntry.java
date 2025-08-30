package com.bensiebert.codelib.ratelimiting;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitEntry {

    private final AtomicInteger calls = new AtomicInteger(0);
    private volatile long windowStart;

    public RateLimitEntry(long now) {
        this.windowStart = now;
    }

    public synchronized boolean allow(int limit, int interval, long now) {
        if(now - windowStart >= interval) {
            windowStart = now;
            calls.set(0);
        }

        return calls.incrementAndGet() <= limit;
    }
}
