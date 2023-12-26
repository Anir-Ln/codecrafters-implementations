package com.anirln.redis.protocol;

import java.nio.charset.StandardCharsets;

public class RedisBytes {
    private final byte[] bytes;
    private final String str;

    public RedisBytes(byte[] bytes) {
        this.bytes = bytes;
        this.str = new String(bytes, StandardCharsets.UTF_8);
    }

    public RedisBytes(String str) {
        this.str = str;
        this.bytes = str.getBytes(StandardCharsets.UTF_8);
    }

    public byte getFirstByte() {
        return this.bytes[0];
    }

    public int length() {
        return this.bytes.length;
    }

    public String toSubString(int beginIndex) {
        return this.str.substring(beginIndex);
    }

    @Override
    public String toString() {
        return str;
    }
}
