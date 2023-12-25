package com.anirln.redis.protocol;

import java.nio.ByteBuffer;
import java.util.List;

import static com.anirln.redis.protocol.RedisDataType.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RedisSerializer {

    public static byte[] encodeRedisData(RedisData data) {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        encodeRedisData(buf, data);
        // to reading mode
        buf.flip();
        byte[] content = new byte[buf.remaining()];
        buf.get(content);
        return content;
    }

    public static void encodeRedisData(ByteBuffer buf, RedisData data) {
        switch (data.type()) {
            case ARRAY -> {
                List<RedisData> array = data.value();
                byte[] size = String.valueOf(array.size()).getBytes(UTF_8);
                buf.put(ARRAY.firstByte()).put(size).put(DELIMITER);
                array.forEach(dataItem -> encodeRedisData(buf, dataItem));
            }
            case BULK_STRING -> {
                String str = data.value();
                buf.put(BULK_STRING.firstByte()).putInt(str.length()).put(DELIMITER).put(str.getBytes(UTF_8)).put(DELIMITER);
            }
            case STRING -> {
                String str = data.value();
                buf.put(STRING.firstByte()).put(str.getBytes(UTF_8)).put(DELIMITER);
            }
            case INTEGER -> {
                int value = data.value();
                buf.put(INTEGER.firstByte()).put(String.valueOf(value).getBytes(UTF_8)).put(DELIMITER);
            }
        }
    }

}
