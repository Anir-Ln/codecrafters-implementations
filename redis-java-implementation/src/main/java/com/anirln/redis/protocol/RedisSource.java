package com.anirln.redis.protocol;

public interface RedisSource {
    int available();

    /**
     * @return the bytes from the buffer until \r\n, skips these bytes.
     * example: "+hello world"
     */
    RedisBytes readLine();
    String readString(int length);
}
