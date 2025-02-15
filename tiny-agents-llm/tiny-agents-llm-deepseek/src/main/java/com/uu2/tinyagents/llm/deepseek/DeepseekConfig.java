package com.uu2.tinyagents.llm.deepseek;

import com.uu2.tinyagents.core.llm.LlmConfig;

public class DeepseekConfig extends LlmConfig {
    private static final String DEFAULT_MODEL = "deepseek-chat";
    private static final String DEFAULT_EMBEDDING_MODEL = "";
    private static final String DEFAULT_ENDPOINT = "https://api.deepseek.com";

    public DeepseekConfig() {
        setEndpoint(DEFAULT_ENDPOINT);
        setModel(DEFAULT_MODEL);
    }

    public DeepseekConfig(String model) {
        this();
        setModel(model);
    }
}
