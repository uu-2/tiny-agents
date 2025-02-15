
package com.uu2.tinyagents.llm.openai;


import com.uu2.tinyagents.core.llm.LlmConfig;

public class OpenAILlmConfig extends LlmConfig {

    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final String DEFAULT_EMBEDDING_MODEL = "text-embedding-ada-002";
    private static final String DEFAULT_ENDPOINT = "https://api.openai.com";

    private String defaultEmbeddingModel = DEFAULT_EMBEDDING_MODEL;

    public String getDefaultEmbeddingModel() {
        return defaultEmbeddingModel;
    }

    public void setDefaultEmbeddingModel(String defaultEmbeddingModel) {
        this.defaultEmbeddingModel = defaultEmbeddingModel;
    }

    public OpenAILlmConfig() {
        setEndpoint(DEFAULT_ENDPOINT);
        setModel(DEFAULT_MODEL);
    }

}
