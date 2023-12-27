package com.anirln.redis.data;

import java.util.HashMap;
import java.util.Map;

public class SimpleDatabase implements Database {
    private final Map<String, DatabaseValue> map;

    public SimpleDatabase() {
        this.map = new HashMap<>();
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public DatabaseValue get(String key) {
        return map.get(key);
    }

    @Override
    public DatabaseValue set(String key, DatabaseValue value) {
        return map.put(key, value);
    }

    @Override
    public DatabaseValue remove(String key) {
        return map.remove(key);
    }
}
