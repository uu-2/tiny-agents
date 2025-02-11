package com.uu2.tinyagents.core.llm;

import lombok.Getter;

@Getter
public abstract class BaseLlm<T extends LlmConfig> implements Llm {
    protected T config;

    public BaseLlm(T config) {
        this.config = config;
    }

}
