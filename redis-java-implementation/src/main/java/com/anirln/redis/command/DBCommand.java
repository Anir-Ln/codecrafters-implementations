package com.anirln.redis.command;


@FunctionalInterface
public interface DBCommand {
    void execute(RespRequest request);
}
