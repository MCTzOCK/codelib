package com.bensiebert.codelib.ratelimiting;

import com.bensiebert.codelib.ratelimiting.springdoc.Error429Response;
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
                .body(new Error429Response("Your request has been rate limited.",
                        Integer.parseInt(ex.getMessage().split(",")[0]),
                        Integer.parseInt(ex.getMessage().split(",")[1])));
    }
}
