package com.bensiebert.codelib.ratelimiting.springdoc;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Error429Response {
    public String error;
    public int maxCalls, perSeconds;
}
