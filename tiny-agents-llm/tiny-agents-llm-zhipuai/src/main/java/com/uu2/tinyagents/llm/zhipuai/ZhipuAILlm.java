
package com.uu2.tinyagents.llm.zhipuai;

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
import com.uu2.tinyagents.core.util.Maps;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZhipuAILlm extends BaseLlm<ZhipuAILlmConfig> {

    private HttpClient httpClient = new HttpClient();
    public AiMessageParser aiMessageParser = ZhipuAILlmUtil.getAiMessageParser(false);
    public AiMessageParser aiStreamMessageParser = ZhipuAILlmUtil.getAiMessageParser(true);

    public ZhipuAILlm(ZhipuAILlmConfig config) {
        super(config);
    }

    /**
     * 文档参考：https://www.bigmodel.cn/dev/api/vector/embedding
     *
     * @param documents 文档内容
     * @return 返回向量数据
     */
    @Override
    public EmbedData embed(EmbeddingOptions options, String... documents) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", ZhipuAILlmUtil.createAuthorizationToken(config));

        String endpoint = config.getEndpoint();
        String payload = Maps.of("model", config.getEmbeddingModel())
                .set("input", documents)
                .toJSON();
        String response = httpClient.post(endpoint + "/api/paas/v4/embeddings", headers, payload);

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


    @Override
    public AiMessageResponse chat(Prompt prompt, ChatOptions options) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", ZhipuAILlmUtil.createAuthorizationToken(config));

        String endpoint = config.getEndpoint();
        String payload = ZhipuAILlmUtil.promptToPayload(prompt, config, false, options);
        String response = httpClient.post(endpoint + "/api/paas/v4/chat/completions", headers, payload);

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
        headers.put("Authorization", ZhipuAILlmUtil.createAuthorizationToken(config));

        String payload = ZhipuAILlmUtil.promptToPayload(prompt, config, true, options);

        String endpoint = config.getEndpoint();
        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, aiStreamMessageParser);
        llmClient.start(endpoint + "/api/paas/v4/chat/completions", headers, payload, clientListener, config);
    }

}
