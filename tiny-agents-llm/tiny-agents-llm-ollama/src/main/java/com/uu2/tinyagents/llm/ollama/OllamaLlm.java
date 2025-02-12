
package com.uu2.tinyagents.llm.ollama;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.uu2.tinyagents.core.llm.*;
import com.uu2.tinyagents.core.llm.client.BaseLlmClientListener;
import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.llm.client.LlmClient;
import com.uu2.tinyagents.core.llm.client.LlmClientListener;
import com.uu2.tinyagents.core.llm.client.impl.DnjsonClient;
import com.uu2.tinyagents.core.llm.client.impl.SseClient;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingOptions;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.util.Maps;
import com.uu2.tinyagents.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OllamaLlm extends BaseLlm<OllamaLlmConfig> {

    private HttpClient httpClient = new HttpClient();
    private final DnjsonClient dnjsonClient = new DnjsonClient();
    public AiMessageParser aiMessageParser = OllamaLlmUtil.getAiMessageParser();


    public OllamaLlm(OllamaLlmConfig config) {
        super(config);
    }


    @Override
    public EmbedData embed(EmbeddingOptions options, String... documents) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        if (StringUtil.hasText(getConfig().getApiKey())) {
            headers.put("Authorization", "Bearer " + getConfig().getApiKey());
        }

        String payload = Maps.of("model", options.getModelOrDefault(config.getModel()))
                .set("input", documents)
                .toJSON();

        String endpoint = config.getEndpoint();
        // https://github.com/ollama/ollama/blob/main/docs/api.md#generate-embeddings
        String response = httpClient.post(endpoint + "/api/embed", headers, payload);

        if (config.isDebug()) {
            System.out.println(">>>>receive payload:" + response);
        }

        if (StringUtil.noText(response)) {
            return null;
        }
        EmbedData vectorData = new EmbedData();

        double[][] embedding = JSONPath.read(response, "$.embeddings", double[][].class);
        vectorData.setVector(embedding);

        vectorData.addMetadata("total_duration", JSONPath.read(response, "$.total_duration", Long.class));
        vectorData.addMetadata("load_duration", JSONPath.read(response, "$.load_duration", Long.class));
        vectorData.addMetadata("prompt_eval_count", JSONPath.read(response, "$.prompt_eval_count", Integer.class));
        vectorData.addMetadata("model", JSONPath.read(response, "$.model", String.class));

        return vectorData;
    }

    @Override
    public AiMessageResponse chat(Prompt prompt, ChatOptions options) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + config.getApiKey());

        String endpoint = config.getEndpoint();
        String payload = OllamaLlmUtil.promptToPayload(prompt, config, options, false);
        String response = httpClient.post(endpoint + "/api/chat", headers, payload);

        log.debug(">>>>receive payload:{}", response);

        if (StringUtil.noText(response)) {
            return AiMessageResponse.error(prompt, response, "no content for response.");
        }

        JSONObject jsonObject = JSON.parseObject(response);
        String error = jsonObject.getString("error");

        AiMessageResponse messageResponse = new AiMessageResponse(prompt, response, aiMessageParser.parse(jsonObject));

        if (error != null && !error.isEmpty()) {
            messageResponse.setError(true);
            messageResponse.setErrorMessage(error);
        }

        return messageResponse;
    }


    @Override
    public void chatStream(Prompt prompt, StreamResponseListener listener, ChatOptions options) {
        LlmClient llmClient = new SseClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + config.getApiKey());

        String payload = OllamaLlmUtil.promptToPayload(prompt, config, options, true);

        String endpoint = config.getEndpoint();
        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, aiMessageParser);
        dnjsonClient.start(endpoint + "/api/chat", headers, payload, clientListener, config);
    }

}
