
package com.uu2.tinyagents.llm.zhipuai;


import com.uu2.tinyagents.core.llm.LlmConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ZhipuAILlmConfig extends LlmConfig {

	private static final String DEFAULT_MODEL = "glm-4-plus";
	private static final String DEFAULT_ENDPOINT = "https://open.bigmodel.cn";
	private static final String DEFAULT_EMBEDDING_MODEL = "embedding-3";

	private String embeddingModel;

	public ZhipuAILlmConfig() {
		setEndpoint(DEFAULT_ENDPOINT);
		setModel(DEFAULT_MODEL);
		setEmbeddingModel(DEFAULT_EMBEDDING_MODEL);
	}
}
