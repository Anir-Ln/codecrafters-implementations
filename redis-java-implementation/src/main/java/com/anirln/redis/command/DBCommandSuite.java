package com.anirln.redis.command;

import java.util.HashMap;
import java.util.Map;

public class DBCommandSuite {
    private final Map<String, DBCommand> commands;

    public DBCommandSuite() {
        this.commands = new HashMap<>();
    }
}
