package com.uu2.tinyagents.llm.siliconflow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SiliconFlowLlm extends BaseLlm<SiliconFlowLlmConfig> {
    private final HttpClient httpClient = new HttpClient();
    public AiMessageParser aiMessageParser = SiliconFlowLlmUtil.getAiMessageParser(false);
    public AiMessageParser streamMessageParser = SiliconFlowLlmUtil.getAiMessageParser(true);

    public static SiliconFlowLlm of(String apiKey) {
        SiliconFlowLlmConfig config = new SiliconFlowLlmConfig();
        config.setApiKey(apiKey);
        return new SiliconFlowLlm(config);
    }

    public static SiliconFlowLlm of(String apiKey, String endpoint) {
        SiliconFlowLlmConfig config = new SiliconFlowLlmConfig();
        config.setApiKey(apiKey);
        config.setEndpoint(endpoint);
        return new SiliconFlowLlm(config);
    }

    public SiliconFlowLlm(SiliconFlowLlmConfig config) {
        super(config);
    }

    @Override
    public AiMessageResponse chat(Prompt prompt, ChatOptions options) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + getConfig().getApiKey());

        Consumer<Map<String, String>> headersConfig = config.getHeadersConfig();
        if (headersConfig != null) {
            headersConfig.accept(headers);
        }

        String payload = SiliconFlowLlmUtil.promptToPayload(prompt, config, options, false);
        String endpoint = config.getEndpoint();
        String response = httpClient.post(endpoint + "/v1/chat/completions", headers, payload);

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
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + getConfig().getApiKey());

        String payload = SiliconFlowLlmUtil.promptToPayload(prompt, config, options, true);
        String endpoint = config.getEndpoint();
        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, streamMessageParser);
        llmClient.start(endpoint + "/v1/chat/completions", headers, payload, clientListener, config);
    }


    @Override
    public EmbedData embed(EmbeddingOptions options, String... documents) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + getConfig().getApiKey());

        String payload = SiliconFlowLlmUtil.promptToEmbeddingsPayload(documents[0], options, config);
        String endpoint = config.getEndpoint();
        // https://docs.siliconflow.cn/cn/api-reference/embeddings/create-embeddings
        String response = httpClient.post(endpoint + "/v1/embeddings", headers, payload);

        if (config.isDebug()) {
            System.out.println(">>>>receive payload:" + response);
        }

        if (StringUtil.noText(response)) {
            return null;
        }

        EmbedData vectorData = new EmbedData();
        vectorData.setMetadataMap(JSONPath.read(response, "$.usage", Map.class));
        List<Object> embeddingList = JSONPath.read(response, "$.data", ArrayList.class);
        double[][] vectors = new double[embeddingList.size()][];
        for (int i = 0; i < embeddingList.size(); i++) {
            vectors[i] = JSONPath.read(response, "$.data[" + i + "].embedding", double[].class);
        }

        vectorData.setVector(vectors);

        return vectorData;
    }
}
