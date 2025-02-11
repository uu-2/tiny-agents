
package com.uu2.tinyagents.core.llm;


import com.uu2.tinyagents.core.llm.client.LlmClient;
import com.uu2.tinyagents.core.message.AiMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class ChatContext {
    private Llm llm;
    private LlmClient client;
    private final Map<String, Object> params = new HashMap<>();

    public ChatContext() {
    }

    public ChatContext(Llm llm, LlmClient client) {
        this.llm = llm;
        this.client = client;
    }

    public void addLastAiMessage(AiMessage aiMessageContent) {
        addParam("lastAiMessage", aiMessageContent);
    }

    public AiMessage getLastAiMessage() {
        return (AiMessage) getParam("lastAiMessage");
    }

    public ChatContext addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public Object getParam(String key) {
        return params.get(key);
    }
}
