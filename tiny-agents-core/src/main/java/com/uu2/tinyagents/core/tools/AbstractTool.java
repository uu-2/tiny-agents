package com.uu2.tinyagents.core.tools;

import lombok.Getter;

@Getter
public abstract class AbstractTool {
    private final String type;

    public AbstractTool(String type) {
        this.type = type;
    }
}
