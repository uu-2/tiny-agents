
package com.uu2.tinyagents.llm.siliconflow;


import com.uu2.tinyagents.core.llm.LlmConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SiliconFlowLlmConfig extends LlmConfig {

    private static final String DEFAULT_MODEL = "deepseek-ai/DeepSeek-V3";
    private static final String DEFAULT_EMBEDDING_MODEL = "BAAI/bge-large-zh-v1.5";
    private static final String DEFAULT_ENDPOINT = "https://api.siliconflow.cn";

    private String defaultEmbeddingModel = DEFAULT_EMBEDDING_MODEL;

    public SiliconFlowLlmConfig() {
        setEndpoint(DEFAULT_ENDPOINT);
        setModel(DEFAULT_MODEL);
    }

}
