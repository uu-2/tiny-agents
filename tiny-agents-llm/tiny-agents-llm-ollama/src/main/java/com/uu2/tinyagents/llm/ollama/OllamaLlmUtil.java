
package com.uu2.tinyagents.llm.ollama;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.impl.DefaultAiMessageParser;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.MessageStatus;
import com.uu2.tinyagents.core.prompt.DefaultPromptFormat;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.PromptFormat;
import com.uu2.tinyagents.core.util.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OllamaLlmUtil {


    private static final PromptFormat promptFormat = new DefaultPromptFormat() {

    };


    public static AiMessageParser getAiMessageParser() {
        DefaultAiMessageParser aiMessageParser = new DefaultAiMessageParser();
        aiMessageParser.setContentPath("$.message.content");
        aiMessageParser.setTotalTokensPath("$.eval_count");
        aiMessageParser.setCompletionTokensPath("$.prompt_eval_count");

        aiMessageParser.setStatusParser(content -> {
            Boolean done = (Boolean) JSONPath.eval(content, "$.done");
            if (done != null && done) {
                return MessageStatus.END;
            }
            return MessageStatus.MIDDLE;
        });

        aiMessageParser.setCallsParser(content -> {
            JSONArray toolCalls = (JSONArray) JSONPath.eval(content, "$.message.tool_calls");
            if (toolCalls == null || toolCalls.isEmpty()) {
                return Collections.emptyList();
            }
            List<AiFunctionCall> aiFunctionCalls = new ArrayList<>();
            for (int i = 0; i < toolCalls.size(); i++) {
                JSONObject jsonObject = toolCalls.getJSONObject(i);
                JSONObject functionObject = jsonObject.getJSONObject("function");
                if (functionObject != null) {
                    AiFunctionCall aiFunctionCall = new AiFunctionCall();
                    aiFunctionCall.setName(functionObject.getString("name"));
                    Object arguments = functionObject.get("arguments");
                    if (arguments instanceof Map) {
                        //noinspection unchecked
                        aiFunctionCall.setArgs((Map<String, Object>) arguments);
                    } else if (arguments instanceof String) {
                        //noinspection unchecked
                        aiFunctionCall.setArgs(JSON.parseObject(arguments.toString(), Map.class));
                    }
                    aiFunctionCall.setIndex(i);
                    if (jsonObject.getString("id") == null) {
                        aiFunctionCall.setCallId(String.valueOf(i));
                    } else {
                        aiFunctionCall.setCallId(jsonObject.getString("id"));
                    }
                    aiFunctionCalls.add(aiFunctionCall);
                }
            }
            return aiFunctionCalls;
        });
        return aiMessageParser;
    }


    public static String promptToPayload(Prompt prompt, OllamaLlmConfig config, ChatOptions options, boolean stream) {
        return Maps.of("model", config.getModel())
                .set("messages", promptFormat.messagesFormat(prompt))
                .setIf(!stream, "stream", stream)
                .setIfNotEmpty("tools", promptFormat.toolsFormat(prompt))
                .setIfNotEmpty("options.seed", options.getSeed())
                .setIfNotEmpty("options.top_k", options.getTopK())
                .setIfNotEmpty("options.top_p", options.getTopP())
                .setIfNotEmpty("options.temperature", options.getTemperature())
                .setIfNotEmpty("options.stop", options.getStop())
                .toJSON();
    }

}
