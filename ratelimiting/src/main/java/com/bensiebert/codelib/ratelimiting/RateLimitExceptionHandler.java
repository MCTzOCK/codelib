package com.bensiebert.codelib.ratelimiting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RateLimitExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handle(RateLimitExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new Object() {
                    public final String error = "Your request has been rate limited.";
                    public final Integer maxCalls = Integer.parseInt(ex.getMessage().split(",")[0]);
                    public final Integer perSeconds = Integer.parseInt(ex.getMessage().split(",")[1]);
                });
    }
}
