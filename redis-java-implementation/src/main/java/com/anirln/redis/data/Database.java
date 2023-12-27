package com.anirln.redis.data;


public interface Database {
    boolean containsKey(String key);
    DatabaseValue get(String key);
    DatabaseValue set(String key, DatabaseValue value);
    DatabaseValue remove(String key);
}

