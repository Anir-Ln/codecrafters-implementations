package com.anirln.redis.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.ByteProcessor;

import java.util.List;

public class RedisDecoder extends ReplayingDecoder<RedisData> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(parseResponse(in));
    }

    private RedisData parseResponse(ByteBuf in) {
        RedisData data = new RedisParser(new NettyRedisSource(this, in)).next();
        checkpoint();
        return data;
    }

    private RedisBytes readLine(ByteBuf buffer) {
        int eol = findEndOfLine(buffer);
        int size = eol - buffer.readerIndex();
        byte[] bytes = new byte[size];
        buffer.readBytes(bytes);
        // skip \r\n
        buffer.skipBytes(2);
        return new RedisBytes(bytes);
    }

    private static int findEndOfLine(final ByteBuf buffer) {
        int i = buffer.forEachByte(ByteProcessor.FIND_CRLF);
        System.out.println("findEndOfLine: " + i +  " " + buffer.getByte(i));
        if (i > 0 && buffer.getByte(i-1) == '\r')
            i--;
        return i;
    }

    private static class NettyRedisSource implements RedisSource {
        RedisDecoder decoder;
        ByteBuf buffer;

        public NettyRedisSource(RedisDecoder decoder, ByteBuf buffer) {
            this.decoder = decoder;
            this.buffer = buffer;
        }

        @Override
        public int available() {
            return decoder.actualReadableBytes();
        }

        @Override
        public RedisBytes readLine() {
            return decoder.readLine(this.buffer);
        }

        @Override
        public String readString(int length) {
            return null;
        }
    }
}
