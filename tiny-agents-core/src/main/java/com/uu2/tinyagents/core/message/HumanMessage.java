package com.uu2.tinyagents.core.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HumanMessage extends Message {
    private Object toolChoice;

    public HumanMessage(String content) {
        super(Role.USER.getRole(), content);
    }

    public static HumanMessage of(String content) {
        return new HumanMessage(content);
    }
}
