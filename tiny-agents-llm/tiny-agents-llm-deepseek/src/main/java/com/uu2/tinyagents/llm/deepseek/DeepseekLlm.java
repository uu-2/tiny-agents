package com.uu2.tinyagents.llm.deepseek;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uu2.tinyagents.core.llm.BaseLlm;
import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.StreamResponseListener;
import com.uu2.tinyagents.core.llm.client.BaseLlmClientListener;
import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.llm.client.LlmClient;
import com.uu2.tinyagents.core.llm.client.LlmClientListener;
import com.uu2.tinyagents.core.llm.client.impl.SseClient;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingOptions;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DeepseekLlm extends BaseLlm<DeepseekConfig> {
    private final Map<String, String> headers = new HashMap<>();
    private final HttpClient httpClient = new HttpClient();
    private final AiMessageParser aiMessageParser = DeepseekLlmUtil.getAiMessageParser(false);
    private final AiMessageParser streamMessageParser = DeepseekLlmUtil.getAiMessageParser(true);

    public DeepseekLlm(DeepseekConfig config) {
        super(config);
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + getConfig().getApiKey());
    }

    public static DeepseekLlm of(String apiKey) {
        DeepseekConfig config = new DeepseekConfig();
        config.setApiKey(apiKey);
        return new DeepseekLlm(config);
    }

    @Override
    public AiMessageResponse chat(Prompt prompt, ChatOptions options) {
        Consumer<Map<String, String>> headersConfig = config.getHeadersConfig();
        if (headersConfig != null) {
            headersConfig.accept(headers);
        }

        String payload = DeepseekLlmUtil.promptToPayload(prompt, config, options, false);
        String endpoint = config.getEndpoint();
        String response = httpClient.post(endpoint + "/chat/completions", headers, payload);

        if (config.isDebug()) {
            System.out.println(">>>>receive payload:" + response);
        }

        if (StringUtil.noText(response)) {
            return AiMessageResponse.error(prompt, response, "no content for response.");
        }

        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject error = jsonObject.getJSONObject("error");

        AiMessageResponse messageResponse = new AiMessageResponse(prompt, response, aiMessageParser.parse(jsonObject));
        if (error != null && !error.isEmpty()) {
            messageResponse.setError(true);
            messageResponse.setErrorMessage(error.getString("message"));
            messageResponse.setErrorType(error.getString("type"));
            messageResponse.setErrorCode(error.getString("code"));
        }

        return messageResponse;
    }

    @Override
    public void chatStream(Prompt prompt, StreamResponseListener listener, ChatOptions options) {
        LlmClient llmClient = new SseClient();
        String payload = DeepseekLlmUtil.promptToPayload(prompt, config, options, true);
        String endpoint = config.getEndpoint();
        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, streamMessageParser);
        llmClient.start(endpoint + "/chat/completions", headers, payload, clientListener, config);
    }

    @Override
    public EmbedData embed(EmbeddingOptions options, String... documents) {
        throw new UnsupportedOperationException("not support embed for DeepseekLlm.");
    }
}
