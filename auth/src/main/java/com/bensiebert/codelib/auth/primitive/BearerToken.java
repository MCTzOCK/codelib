package com.bensiebert.codelib.auth.primitive;

public class BearerToken {

    public static String extract(String header) {
        if(header == null) return "";
        if(header.startsWith("Bearer ")) {
            return header.replaceFirst("Bearer ", "");
        }
        return header;
    }
}
