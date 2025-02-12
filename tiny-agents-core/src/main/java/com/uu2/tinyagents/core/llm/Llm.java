package com.uu2.tinyagents.core.llm;

import com.uu2.tinyagents.core.llm.embedding.EmbeddingModel;
import com.uu2.tinyagents.core.llm.exception.LlmException;
import com.uu2.tinyagents.core.llm.response.AbstractMessageResponse;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.TextPrompt;

public interface Llm extends EmbeddingModel {
    default String chat(String prompt) {
        return chat(prompt, ChatOptions.DEFAULT);
    }

    default String chat(String prompt, ChatOptions options) {
        AbstractMessageResponse<AiMessage> response = chat(new TextPrompt(prompt), options);
        if (response != null && response.isError()) throw new LlmException(response.getErrorMessage());
        return response != null && response.getMessage() != null ? String.valueOf(response.getMessage().getContent()) : null;
    }

    default AiMessageResponse chat(Prompt prompt) {
        return chat(prompt, ChatOptions.DEFAULT);
    }

    AiMessageResponse chat(Prompt prompt, ChatOptions options);

    default void chatStream(String prompt, StreamResponseListener listener) {
        this.chatStream(new TextPrompt(prompt), listener, ChatOptions.DEFAULT);
    }

    default void chatStream(String prompt, StreamResponseListener listener, ChatOptions options) {
        this.chatStream(new TextPrompt(prompt), listener, options);
    }

    //chatStream
    default void chatStream(Prompt prompt, StreamResponseListener listener) {
        this.chatStream(prompt, listener, ChatOptions.DEFAULT);
    }

    void chatStream(Prompt prompt, StreamResponseListener listener, ChatOptions options);
}
