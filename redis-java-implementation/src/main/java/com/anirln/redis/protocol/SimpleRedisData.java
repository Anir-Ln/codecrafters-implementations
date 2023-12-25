package com.anirln.redis.protocol;

import java.util.Objects;

public record SimpleRedisData<T>(RedisDataType type, T value) implements RedisData {

    @Override
    public boolean equals(RedisData o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleRedisData<?> that = (SimpleRedisData<?>) o;
        return type == that.type && Objects.equals(value, that.value);
    }

}
