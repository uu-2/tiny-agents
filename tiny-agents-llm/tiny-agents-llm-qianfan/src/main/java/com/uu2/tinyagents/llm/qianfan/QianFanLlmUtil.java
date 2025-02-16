package com.uu2.tinyagents.llm.qianfan;

import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingOptions;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.impl.DefaultAiMessageParser;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.prompt.DefaultPromptFormat;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.PromptFormat;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.tools.function.Parameter;
import com.uu2.tinyagents.core.util.CollectionUtil;
import com.uu2.tinyagents.core.util.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QianFanLlmUtil {
    private static final PromptFormat promptFormat = new DefaultPromptFormat() {
        @Override
        protected List<Object> buildFunctionJsonArray(List<Function> functions) {
            List<Object> functionsJsonArray = new ArrayList<>();
            for (Function function : functions) {
                Map<String, Object> functionRoot = new HashMap<>();
                functionRoot.put("type", "function");

                Map<String, Object> functionObj = new HashMap<>();
                functionRoot.put("function", functionObj);

                functionObj.put("name", function.getName());
                functionObj.put("description", function.getDescription());


                Map<String, Object> parametersObj = new HashMap<>();
                functionObj.put("parameters", parametersObj);

                parametersObj.put("type", "object");

                Map<String, Object> propertiesObj = new HashMap<>();
                parametersObj.put("properties", propertiesObj);

                List<String> requiredProperties = new ArrayList<>();

                for (Parameter parameter : function.getParameters()) {
                    Map<String, Object> parameterObj = new HashMap<>();
                    parameterObj.put("type", parameter.getType());
                    parameterObj.put("description", parameter.getDescription());
                    if (parameter.getEnums().length > 0) {
                        parameterObj.put("enum", parameter.getEnums());
                    }
                    if (parameter.isRequired()) {
                        requiredProperties.add(parameter.getName());
                    }

                    propertiesObj.put(parameter.getName(), parameterObj);
                }

                if (!requiredProperties.isEmpty()) {
                    parametersObj.put("required", requiredProperties);
                }

                functionsJsonArray.add(functionRoot);
            }
            return functionsJsonArray;
        }
    };


    public static AiMessageParser getAiMessageParser(boolean isStream) {
        return DefaultAiMessageParser.getChatGPTMessageParser(isStream);
    }


    public static String promptToEmbeddingsPayload(EmbeddingOptions options, QianFanLlmConfig config, String... documents) {
        return Maps.of("model", options.getModelOrDefault(config.getEmbeddingModel()))
                .set("encoding_format", "float")
                .set("input", documents)
                .toJSON();
    }


    public static String promptToPayload(Prompt prompt, QianFanLlmConfig config, ChatOptions options, boolean withStream) {
        List<Message> messages = prompt.messages();
        Object toolChoice = "auto";

        if(CollectionUtil.lastItem(messages) instanceof HumanMessage humanMessage) {
            toolChoice = humanMessage.getToolChoice();
        }
        return Maps.of("model", config.getModel())
                .set("messages", promptFormat.messagesFormat(prompt))
                .setIf(withStream, "stream", true)
                .setIfNotEmpty("tools", promptFormat.toolsFormat(prompt))
                .setIfContainsKey("tools", "tool_choice", toolChoice)
                .setIfNotNull("top_p", options.getTopP())
                .setIfNotEmpty("stop", options.getStop())
                .setIf(map -> !map.containsKey("tools") && options.getTemperature() > 0, "temperature", options.getTemperature())
                .setIf(map -> !map.containsKey("tools") && options.getMaxTokens() != null, "max_tokens", options.getMaxTokens())
                .toJSON();
    }
}



