package com.anir.redis.protocol;

import com.anirln.redis.protocol.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.when;

public class RedisParserTests {
    private final RedisSource redisSource = Mockito.mock(RedisSource.class);
    private final RedisParser redisParser = new RedisParser(redisSource);

    @Test
    public void testParseRedisDataString() {
        // "+hello world"
        byte[] helloWorldBytes = {43, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
        RedisData helloWorldData = new SimpleRedisData<>(RedisDataType.STRING, "hello world");

        when(redisSource.readLine()).thenReturn(helloWorldBytes);

        RedisData data = redisParser.next();
        Assertions.assertEquals(helloWorldData, data);
    }

    @Test
    public void testParseRedisDataInteger() {
        byte[] numberTenBytes = {58, 49, 48};
        RedisData numberTenData = new SimpleRedisData<>(RedisDataType.INTEGER, 10);
        when(redisSource.readLine()).thenReturn(numberTenBytes);

        RedisData data = redisParser.next();
        Assertions.assertEquals(numberTenData, data);
    }

    @Test
    public void testParseRedisDataArray() {
        byte[][] arrayBytes = {
                {42, 51},
                {43, 118, 97, 108, 117, 101, 49},
                {43, 118, 97, 108, 117, 101, 50},
                {58, 49, 48}
        };
        RedisData arrayRedisData = new SimpleRedisData<>(RedisDataType.ARRAY, List.of(
            new SimpleRedisData<>(RedisDataType.STRING, "value1"),
            new SimpleRedisData<>(RedisDataType.STRING, "value2"),
            new SimpleRedisData<>(RedisDataType.INTEGER, 10)
        ));

        when(redisSource.readLine()).thenReturn(arrayBytes[0], arrayBytes[1], arrayBytes[2], arrayBytes[3]);

        RedisData data = redisParser.next();
        Assertions.assertEquals(arrayRedisData, data);
    }


}
