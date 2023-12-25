package com.anirln.redis.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RedisEncoder extends MessageToByteEncoder<RedisData> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RedisData msg, ByteBuf out) throws Exception {
        out.writeBytes(RedisSerializer.encodeRedisData(msg));
    }
}
