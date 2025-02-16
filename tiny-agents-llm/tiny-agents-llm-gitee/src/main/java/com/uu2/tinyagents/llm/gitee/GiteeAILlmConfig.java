
package com.uu2.tinyagents.llm.gitee;


import com.uu2.tinyagents.core.llm.LlmConfig;

public class GiteeAILlmConfig extends LlmConfig {

    private static final String DEFAULT_MODEL = "Qwen2-7B-Instruct";
    private static final String DEFAULT_EMBEDDING_MODEL = "bge-large-zh-v1.5";
    private static final String DEFAULT_ENDPOINT = "https://ai.gitee.com";

    private String defaultEmbeddingModal = DEFAULT_EMBEDDING_MODEL;

    public String getDefaultEmbeddingModal() {
        return defaultEmbeddingModal;
    }

    public void setDefaultEmbeddingModal(String defaultEmbeddingModal) {
        this.defaultEmbeddingModal = defaultEmbeddingModal;
    }

    public GiteeAILlmConfig() {
        setEndpoint(DEFAULT_ENDPOINT);
        setModel(DEFAULT_MODEL);
    }

}
