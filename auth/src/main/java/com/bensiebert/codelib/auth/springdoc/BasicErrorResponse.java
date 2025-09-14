package com.bensiebert.codelib.auth.springdoc;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class BasicErrorResponse {
    public String error, message;
}
