package com.anirln.redis.protocol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.anirln.redis.protocol.RedisDataType.*;

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
        RedisBytes line = source.readLine();
        return parseRedisData(line);
    }

    private RedisData parseRedisData(RedisBytes redisBytes) {
        if (redisBytes == null || redisBytes.length() == 0) return null;
        byte firstByte = redisBytes.getFirstByte();
        String data = redisBytes.toSubString(1);
        if (firstByte == STRING.firstByte()) {
            return new SimpleRedisData<>(STRING, data);
        } else if (firstByte == INTEGER.firstByte()) {
            return new SimpleRedisData<>(INTEGER, Integer.parseInt(data));
        } else if (firstByte == ARRAY.firstByte()) {
            int size = Integer.parseInt(data);
            List<RedisData> list = new ArrayList<>(size);
            while(size-- > 0) {
                list.add(next());
            }
            return new SimpleRedisData<>(ARRAY, list);
        }
        return null;
    }
}
