
package com.uu2.tinyagents.llm.moonshot;


import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.impl.DefaultAiMessageParser;
import com.uu2.tinyagents.core.prompt.DefaultPromptFormat;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.PromptFormat;
import com.uu2.tinyagents.core.util.Maps;

public class MoonshotLlmUtil {

    private static final PromptFormat promptFormat = new DefaultPromptFormat();

    public static AiMessageParser getAiMessageParser(Boolean isStream) {
        return DefaultAiMessageParser.getChatGPTMessageParser(isStream);
    }


    /**
     * 将给定的Prompt转换为特定的payload格式，用于与语言模型进行交互。
     *
     * @param prompt      需要转换为 payload 的 Prompt 对象，包含了对话的具体内容。
     * @param config      用于配置 Moonshot LLM 行为的配置对象，例如指定使用的模型。
     * @param isStream    指示 payload 是否应该以流的形式进行处理。
     * @param chatOptions 包含了对话选项的配置，如温度和最大令牌数等。
     * @return 返回一个字符串形式的payload，供进一步的处理或发送给语言模型。
     */
    public static String promptToPayload(Prompt prompt, MoonshotLlmConfig config, Boolean isStream, ChatOptions chatOptions) {
        // 构建payload的根结构，包括模型信息、流式处理标志、对话选项和格式化后的prompt消息。
        return Maps.of("model", config.getModel())
            .set("stream", isStream)
            .set("temperature", chatOptions.getTemperature())
            .set("max_tokens", chatOptions.getMaxTokens())
            .set("messages", promptFormat.messagesFormat(prompt))
            .toJSON();
    }
}
