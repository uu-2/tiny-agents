
package com.uu2.tinyagents.llm.openai;


import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingOptions;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.impl.DefaultAiMessageParser;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.prompt.DefaultPromptFormat;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.PromptFormat;
import com.uu2.tinyagents.core.util.CollectionUtil;
import com.uu2.tinyagents.core.util.Maps;

import java.util.List;

public class OpenAiLlmUtil {

    private static final PromptFormat promptFormat = new DefaultPromptFormat();

    public static AiMessageParser getAiMessageParser(boolean isStream) {
        return DefaultAiMessageParser.getChatGPTMessageParser(isStream);
    }


    public static String promptToEmbeddingsPayload(String document, EmbeddingOptions options, OpenAILlmConfig config) {
        // https://platform.openai.com/docs/api-reference/making-requests
        return Maps.of("model", options.getModelOrDefault(config.getDefaultEmbeddingModel()))
                .set("encoding_format", "float")
                .set("input", document)
                .toJSON();
    }


    public static String promptToPayload(Prompt prompt, OpenAILlmConfig config, ChatOptions options, boolean withStream) {
        List<Message> messages = prompt.messages();
        HumanMessage humanMessage = (HumanMessage) CollectionUtil.lastItem(messages);
        return Maps.of("model", config.getModel())
                .set("messages", promptFormat.messagesFormat(prompt))
                .setIf(withStream, "stream", true)
                .setIfNotEmpty("tools", promptFormat.toolsFormat(prompt))
                .setIfContainsKey("tools", "tool_choice", humanMessage.getToolChoice())
                .setIfNotNull("top_p", options.getTopP())
                .setIfNotEmpty("stop", options.getStop())
                .setIf(map -> !map.containsKey("tools") && options.getTemperature() > 0, "temperature", options.getTemperature())
                .setIf(map -> !map.containsKey("tools") && options.getMaxTokens() != null, "max_tokens", options.getMaxTokens())
                .toJSON();
    }


}
