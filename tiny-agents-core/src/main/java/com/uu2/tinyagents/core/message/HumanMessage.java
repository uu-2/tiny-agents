package com.uu2.tinyagents.core.message;

public class HumanMessage extends Message {
    public HumanMessage(String content) {
        super(Role.USER.getRole(), content);
    }

    public static HumanMessage of(String content) {
        return new HumanMessage(content);
    }
}
