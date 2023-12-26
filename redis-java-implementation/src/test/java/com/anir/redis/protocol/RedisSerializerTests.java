package com.anir.redis.protocol;

import com.anirln.redis.protocol.RedisData;
import com.anirln.redis.protocol.RedisDataType;
import com.anirln.redis.protocol.RedisSerializer;
import com.anirln.redis.protocol.SimpleRedisData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;

public class RedisSerializerTests {

    @Test
    public void testSerializeString() {
        RedisData data = new SimpleRedisData<>(RedisDataType.STRING, "hello world");
        byte[] expectedBytes = "+hello world\r\n".getBytes(UTF_8);

        byte[] bytes = RedisSerializer.encodeRedisData(data);
        Assertions.assertArrayEquals(expectedBytes, bytes);
    }

    @Test
    public void testSerializeInteger() {
        RedisData data = new SimpleRedisData<>(RedisDataType.INTEGER, 10);
        byte[] expectedBytes = ":10\r\n".getBytes();

        byte[] bytes = RedisSerializer.encodeRedisData(data);
        Assertions.assertArrayEquals(expectedBytes, bytes);
    }

    @Test
    public void testSerializeArray() {
        RedisData data = new SimpleRedisData<>(
                RedisDataType.ARRAY,
                List.of(
                        new SimpleRedisData<>(RedisDataType.STRING, "value1"),
                        new SimpleRedisData<>(RedisDataType.STRING, "value2"),
                        new SimpleRedisData<>(RedisDataType.INTEGER, 10)
                )
        );
        byte[] expectedBytes = "*3\r\n+value1\r\n+value2\r\n:10\r\n".getBytes(UTF_8);

        byte[] bytes = RedisSerializer.encodeRedisData(data);
        Assertions.assertArrayEquals(expectedBytes, bytes);
    }
}
