package com.uu2.tinyagents.core.llm;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;

@Data
public class LlmConfig implements Serializable {
    private String model;

    private String endpoint;

    private String apiKey;

    private String apiSecret;

    private boolean debug;

    private Consumer<Map<String, String>> headersConfig;
}
