package com.uu2.tinyagents.llm.deepseek;

import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.LlmConfig;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.impl.DefaultAiMessageParser;
import com.uu2.tinyagents.core.prompt.DefaultPromptFormat;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.PromptFormat;
import com.uu2.tinyagents.core.util.Maps;

public class DeepseekLlmUtil {
    private static final PromptFormat promptFormat = new DefaultPromptFormat();

    public static AiMessageParser getAiMessageParser(boolean isStream) {
        return DefaultAiMessageParser.getChatGPTMessageParser(isStream);
    }



    public static String promptToPayload(Prompt prompt, LlmConfig config, ChatOptions options, boolean withStream) {
        return Maps.of("model", config.getModel())
                .set("messages", promptFormat.messagesFormat(prompt))
                .setIf(withStream, "stream", true)
                .setIfNotEmpty("tools", promptFormat.toolsFormat(prompt))
                //.setIfContainsKey("tools", "tool_choice", humanMessage.getToolChoice())
                .setIfNotNull("top_p", options.getTopP())
                .setIfNotEmpty("stop", options.getStop())
                .setIf(map -> !map.containsKey("tools") && options.getTemperature() > 0, "temperature", options.getTemperature())
                .setIf(map -> !map.containsKey("tools") && options.getMaxTokens() != null, "max_tokens", options.getMaxTokens())
                .toJSON();
    }
}
