package com.uu2.tinyagents.core.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String role;
    private Object content;

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

        public String getRole() {
            return role;
        }
    }
}
