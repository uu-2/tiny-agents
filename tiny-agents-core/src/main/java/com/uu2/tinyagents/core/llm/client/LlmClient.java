
package com.uu2.tinyagents.core.llm.client;

import com.uu2.tinyagents.core.llm.LlmConfig;

import java.util.Map;

public interface LlmClient {

    void start(String url, Map<String, String> headers, String payload, LlmClientListener listener, LlmConfig config);

    void stop();
}
