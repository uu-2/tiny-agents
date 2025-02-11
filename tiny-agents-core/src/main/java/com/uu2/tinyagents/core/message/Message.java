package com.uu2.tinyagents.core.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String role;
    private Object content;

    public Message prompt() {
        return this;
    }

    @Getter
    public enum Role {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        FUNCTION("function"),
        TOOL("tool");

        private final String role;

        Role(String role) {
            this.role = role;
        }

    }
}
