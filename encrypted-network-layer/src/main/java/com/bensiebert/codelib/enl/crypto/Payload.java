package com.bensiebert.codelib.enl.crypto;

public class Payload {

    public String key;
    public String data;

    public Payload(String key, String data) {
        this.key = key;
        this.data = data;
    }

    public String toString() {
        return this.key + "\u0000" + this.data;
    }
}
