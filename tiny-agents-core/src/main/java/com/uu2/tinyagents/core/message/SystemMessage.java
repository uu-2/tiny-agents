package com.uu2.tinyagents.core.message;

public class SystemMessage extends Message {
    SystemMessage(String content) {
        super(Role.SYSTEM.getRole(), content);
    }

    public static SystemMessage of(String content) {
        return new SystemMessage(content);
    }
}
