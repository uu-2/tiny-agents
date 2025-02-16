
package com.uu2.tinyagents.llm.coze;

import com.alibaba.fastjson.JSONPath;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.impl.DefaultAiMessageParser;
import com.uu2.tinyagents.core.message.*;
import com.uu2.tinyagents.core.message.tools.ToolCallsMessage;
import com.uu2.tinyagents.core.message.tools.ToolExecMessage;
import com.uu2.tinyagents.core.prompt.DefaultPromptFormat;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.PromptFormat;
import com.uu2.tinyagents.core.util.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CozeLlmUtil {

    public static final Map<Class<? extends Message>, String> MESSAGE_TYPE = new HashMap<>();

    static {
        MESSAGE_TYPE.put(SystemMessage.class, "question");
        MESSAGE_TYPE.put(HumanMessage.class, "question");
        MESSAGE_TYPE.put(AttachmentMessage.class, "question");
        MESSAGE_TYPE.put(AiMessage.class, "answer");
        MESSAGE_TYPE.put(ToolCallsMessage.class, "function_call");
        MESSAGE_TYPE.put(ToolExecMessage.class, "tool_response");
    }

    public static void addMessageType(Class<? extends Message> clazz, String type) {
        MESSAGE_TYPE.put(clazz, type);
    }

    private static final PromptFormat promptFormat = new DefaultPromptFormat() {
        @Override
        public Object messagesFormat(Prompt prompt) {
            List<Message> messages = prompt.messages();
            if (messages == null || messages.isEmpty()) {
                return null;
            }

            return messages.stream().map(message ->
                    Maps.of("role", message.getRole())
                            .set("content", message.getContent())
                            .set("content_type", message.getContent() instanceof String ? "text" : "object_string")
                            .set("type", MESSAGE_TYPE.getOrDefault(message.getClass(), "unknown")))
                    .toList();
        }
    };

    public static AiMessageParser getAiMessageParser() {
        DefaultAiMessageParser aiMessageParser = new DefaultAiMessageParser();
        aiMessageParser.setContentPath("$.content");
        aiMessageParser.setTotalTokensPath("$.usage.token_count");
        aiMessageParser.setCompletionTokensPath("$.usage.output_count");
        aiMessageParser.setPromptTokensPath("$.usage.input_count");

        aiMessageParser.setStatusParser(content -> {
            Boolean done = (Boolean) JSONPath.eval(content, "$.done");
            if (done != null && done) {
                return MessageStatus.END;
            }
            return MessageStatus.MIDDLE;
        });
        return aiMessageParser;
    }


    public static String promptToPayload(Prompt prompt, String botId, String userId,
                                         Map<String, String> customVariables, boolean autoSaveHistory, boolean stream) {
        return Maps.of()
                .set("bot_id", botId)
                .set("user_id", userId)
                .set("auto_save_history", true)
                .set("additional_messages", promptFormat.messagesFormat(prompt))
                .set("stream", stream)
                .setIf(customVariables != null, "custom_variables", customVariables)
                .toJSON();
    }

}
