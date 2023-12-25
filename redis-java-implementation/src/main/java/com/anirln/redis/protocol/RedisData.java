package com.anirln.redis.protocol;

public interface RedisData {
    RedisDataType type();
    <T> T value();
    boolean equals(RedisData o);
}
