
package com.uu2.tinyagents.core.llm.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uu2.tinyagents.core.llm.*;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.Objects;

public class BaseLlmClientListener implements LlmClientListener {

    private final StreamResponseListener streamResponseListener;
    private final Prompt prompt;
    private final AiMessageParser messageParser;
    private final StringBuilder fullMessage = new StringBuilder();
    private AiMessage lastAiMessage;
    private final ChatContext context;

    public BaseLlmClientListener(Llm llm
        , LlmClient client
        , StreamResponseListener streamResponseListener
        , Prompt prompt
        , AiMessageParser messageParser) {

        this.streamResponseListener = streamResponseListener;
        this.prompt = prompt;
        this.messageParser = messageParser;
        this.context = new ChatContext(llm, client);
    }


    @Override
    public void onStart(LlmClient client) {
        streamResponseListener.onStart(context);
    }

    @Override
    public void onMessage(LlmClient client, String response) {
        if (StringUtil.noText(response) || "[DONE]".equalsIgnoreCase(response.trim())) {
            return;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(response);
            lastAiMessage = messageParser.parse(jsonObject);
            Object content = lastAiMessage.getContent();

            // 第一个和最后一个content都为null
            if (Objects.nonNull(content)) {
                fullMessage.append(content);
            }

            lastAiMessage.setFullContent(fullMessage.toString());
            AiMessageResponse aiMessageResponse = new AiMessageResponse(prompt, response, lastAiMessage);
            streamResponseListener.onMessage(context, aiMessageResponse);
        } catch (Exception err) {
            streamResponseListener.onFailure(context, err);
        }
    }

    @Override
    public void onStop(LlmClient client) {
        if (lastAiMessage != null) {
            this.prompt.processAssistantMessage(lastAiMessage);

        }
        context.addLastAiMessage(lastAiMessage);
        streamResponseListener.onStop(context);
    }

    @Override
    public void onFailure(LlmClient client, Throwable throwable) {
        streamResponseListener.onFailure(context, throwable);
    }

}
