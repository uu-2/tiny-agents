package com.uu2.tinyagents.core.message;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String role;
    private Object content;

    public Object prompt() {
        return this;
    }

    public Message format(Map<String, Object> params) {
        if (content != null && content instanceof String
                && params != null && !params.isEmpty()) {
            this.content = StrUtil.format((String) content, params);
        }
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
