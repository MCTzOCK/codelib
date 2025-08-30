package com.bensiebert.codelib.cache;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class KVCache {

    public static final KVCache DEFAULT = new KVCache("default");

    public static final Long DEFAULT_TTL = -1L;
    public static final Long TTL_1_MINUTE = 60 * 1000L;

    @Getter
    private String name;

    private final List<KVItem> items;

    public KVCache(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public void insertOrUpdate(KVItem item) {
        if (contains(item.getKey())) {
            items.stream().filter(i -> i.getKey().equals(item.getKey())).findFirst().ifPresent(i -> {
                i.setValue(item.getValue());
                i.setTtl(item.getTtl());
            });
        } else {
            items.add(item);
        }
    }

    public void put(String key, Object value) {
        put(key, value, DEFAULT_TTL);
    }

    public void put(String key, Object value, long ttl) {
        insertOrUpdate(new KVItem(key, value, ttl));
    }


    public KVItem get(String key) {
        KVItem item = items.stream().filter(i -> i.getKey().equals(key)).findFirst().orElse(null);
        if (item == null) {
            return null;
        }

        if (item.getTtl() > 0 && System.currentTimeMillis() > item.getTtl()) {
            remove(key);
            return null;
        }
        return item;
    }

    public Object getValue(String key) {
        KVItem item = get(key);
        return item != null ? item.getValue() : null;
    }

    public void remove(String key) {
        items.removeIf(i -> i.getKey().equals(key));
    }

    public boolean contains(String key) {
        KVItem item = items.stream().filter(i -> i.getKey().equals(key)).findFirst().orElse(null);
        if (item == null) {
            return false;
        }
        if (item.getTtl() > 0 && System.currentTimeMillis() > item.getTtl()) {
            remove(key);
            return false;
        }
        return true;
    }

    public static Long getTTL(long ttl) {
        return ttl > 0 ? System.currentTimeMillis() + ttl : -1L;
    }
}
