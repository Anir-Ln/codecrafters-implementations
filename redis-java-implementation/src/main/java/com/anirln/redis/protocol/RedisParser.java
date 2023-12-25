package com.anirln.redis.protocol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.anirln.redis.protocol.RedisDataType.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RedisParser implements Iterator<RedisData> {
    private final RedisSource source;

    public RedisParser(RedisSource source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        return source.available() != 0;
    }

    @Override
    public RedisData next() {
        byte[] line = source.readLine();
        return parseRedisData(line);
    }

    private RedisData parseRedisData(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        byte firstByte = bytes[0];
        if (firstByte == STRING.firstByte()) {
            return new SimpleRedisData<>(STRING, new String(bytes, UTF_8).substring(1));
        } else if (firstByte == INTEGER.firstByte()) {
            return new SimpleRedisData<>(INTEGER, Integer.valueOf(new String(bytes, UTF_8).substring(1)));
        } else if (firstByte == ARRAY.firstByte()) {
            int size = Integer.parseInt(new String(bytes, UTF_8).substring(1));
            List<RedisData> list = new ArrayList<>(size);
            while(size-- > 0) {
                list.add(next());
            }
            return new SimpleRedisData<>(ARRAY, list);
        }
        return null;
    }
}
