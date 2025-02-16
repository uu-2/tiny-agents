
package com.uu2.tinyagents.llm.qwen;

import com.uu2.tinyagents.core.llm.LlmConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QwenLlmConfig extends LlmConfig {

    private static final String DEFAULT_MODEL = "qwen-turbo";
    private static final String DEFAULT_ENDPOINT = "https://dashscope.aliyuncs.com";
    private String defaultEmbeddingModel = "text-embedding-v3";

    public QwenLlmConfig() {
        setEndpoint(DEFAULT_ENDPOINT);
        setModel(DEFAULT_MODEL);
    }

}
