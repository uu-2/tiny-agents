
package com.uu2.tinyagents.llm.moonshot;

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

public class MoonshotLlm extends BaseLlm<MoonshotLlmConfig> {


    HttpClient httpClient = new HttpClient();

    public AiMessageParser aiMessageParser = MoonshotLlmUtil.getAiMessageParser(false);
    public AiMessageParser aiStreamMessageParser = MoonshotLlmUtil.getAiMessageParser(true);

    public MoonshotLlm(MoonshotLlmConfig config) {
        super(config);
    }


    @Override
    public AiMessageResponse chat(Prompt prompt, ChatOptions options) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + config.getApiKey());

        String endpoint = config.getEndpoint();
        String payload = MoonshotLlmUtil.promptToPayload(prompt, config, false, options);
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

        String payload = MoonshotLlmUtil.promptToPayload(prompt, config, true, options);
        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, aiStreamMessageParser);
        String endpoint = config.getEndpoint();
        llmClient.start(endpoint + "/v1/chat/completions", headers, payload, clientListener, config);
    }


    @Override
    public EmbedData embed(EmbeddingOptions options, String... document) {
        throw new IllegalStateException("Moonshot can not support embedding");
    }

}
