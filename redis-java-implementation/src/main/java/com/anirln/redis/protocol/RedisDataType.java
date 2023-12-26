package com.anirln.redis.protocol;

public enum RedisDataType {
    // Map, and Set can be serialized as an array
    STRING((byte) '+'),
    INTEGER((byte) ':'),
    BULK_STRING((byte) '$'),
    ARRAY((byte) '*'),
    ERROR((byte) '-');

    static final byte[] DELIMITER = new byte[] {'\r', '\n'};

    private final byte firstByte;

    RedisDataType(byte firstByte) {
        this.firstByte = firstByte;
    }

    public byte firstByte() {
        return firstByte;
    }
}
