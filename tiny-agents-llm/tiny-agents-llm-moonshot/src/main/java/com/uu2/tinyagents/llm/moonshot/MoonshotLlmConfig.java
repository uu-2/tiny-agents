
package com.uu2.tinyagents.llm.moonshot;


import com.uu2.tinyagents.core.llm.LlmConfig;

public class MoonshotLlmConfig extends LlmConfig {

	private static final String DEFAULT_MODEL = "moonshot-v1-8k";
	private static final String DEFAULT_ENDPOINT = "https://api.moonshot.cn";

	public MoonshotLlmConfig() {
		setEndpoint(DEFAULT_ENDPOINT);
		setModel(DEFAULT_MODEL);
	}

}
