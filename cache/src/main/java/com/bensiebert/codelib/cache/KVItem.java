package com.bensiebert.codelib.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Getter
@AllArgsConstructor
public class KVItem {

    private String key;

    @Setter
    @With
    private Object value;

    @Setter
    @With
    private long ttl;
}
