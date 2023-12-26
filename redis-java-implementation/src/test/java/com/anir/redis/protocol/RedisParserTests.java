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
        RedisBytes helloWorldBytes = new RedisBytes("+hello world");
        RedisData helloWorldData = new SimpleRedisData<>(RedisDataType.STRING, "hello world");

        when(redisSource.readLine()).thenReturn(helloWorldBytes);

        RedisData data = redisParser.next();
        Assertions.assertEquals(helloWorldData, data);
    }

    @Test
    public void testParseRedisDataInteger() {
        RedisBytes numberTenBytes = new RedisBytes(":10");
        RedisData numberTenData = new SimpleRedisData<>(RedisDataType.INTEGER, 10);
        when(redisSource.readLine()).thenReturn(numberTenBytes);

        RedisData data = redisParser.next();
        Assertions.assertEquals(numberTenData, data);
    }

    @Test
    public void testParseRedisDataArray() {
        RedisBytes[] arrayBytes = {
                new RedisBytes("*3"),
                new RedisBytes("+value1"),
                new RedisBytes("+value2"),
                new RedisBytes(":10"),
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
