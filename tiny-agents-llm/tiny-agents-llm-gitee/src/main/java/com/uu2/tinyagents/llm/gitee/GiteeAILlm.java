
package com.uu2.tinyagents.llm.gitee;

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
import com.uu2.tinyagents.core.util.Maps;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GiteeAILlm extends BaseLlm<GiteeAILlmConfig> {

    private final HttpClient httpClient = new HttpClient();
    public AiMessageParser aiMessageParser = GiteeAILlmUtil.getAiMessageParser(false);
    public AiMessageParser streamMessageParser = GiteeAILlmUtil.getAiMessageParser(true);


    public GiteeAILlm(GiteeAILlmConfig config) {
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

        String payload = GiteeAILlmUtil.promptToPayload(prompt, config, options, false);
        if (config.isDebug()) {
            System.out.println(">>>>send payload:" + payload);
        }

        String endpoint = config.getEndpoint();
        String response = httpClient.post(endpoint + "/api/serverless/" + config.getModel() + "/chat/completions", headers, payload);
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

        String payload = GiteeAILlmUtil.promptToPayload(prompt, config, options, true);
        String endpoint = config.getEndpoint();
        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, streamMessageParser);
        llmClient.start(endpoint + "/api/serverless/" + config.getModel() + "/chat/completions", headers, payload, clientListener, config);
    }


    @Override
    public EmbedData embed(EmbeddingOptions options, String...  documents) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + getConfig().getApiKey());

        String payload = Maps.of("inputs", documents).toJSON();
        String endpoint = config.getEndpoint();
        String embeddingModel = options.getModelOrDefault(config.getDefaultEmbeddingModal());
        String response = httpClient.post(endpoint + "/api/serverless/" + embeddingModel + "/embeddings", headers, payload);

        if (config.isDebug()) {
            System.out.println(">>>>receive payload:" + response);
        }

        if (StringUtil.noText(response)) {
            return null;
        }

        EmbedData vectorData = new EmbedData();
        double[][] embedding = JSONObject.parseObject(response, double[][].class);
        vectorData.setVector(embedding);

        return vectorData;
    }


}
