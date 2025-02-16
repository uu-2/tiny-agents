
package com.uu2.tinyagents.llm.spark;

import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.impl.DefaultAiMessageParser;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.MessageStatus;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.prompt.DefaultPromptFormat;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.prompt.PromptFormat;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.tools.function.Parameter;
import com.uu2.tinyagents.core.util.HashUtil;
import com.uu2.tinyagents.core.util.Maps;
import com.alibaba.fastjson.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class SparkLlmUtil {

    private static final PromptFormat promptFormat = new DefaultPromptFormat() {
        @Override
        protected List<Object> buildFunctionJsonArray(List<Function> functions) {
            List<Object> functionsJsonArray = new ArrayList<>();
            for (Function function : functions) {
                Map<String, Object> propertiesMap = new HashMap<>();
                List<String> requiredProperties = new ArrayList<>();

                Parameter[] parameters = function.getParameters();
                if (parameters != null) {
                    for (Parameter parameter : parameters) {
                        if (parameter.isRequired()) {
                            requiredProperties.add(parameter.getName());
                        }
                        propertiesMap.put(parameter.getName(), Maps.of("type", parameter.getType()).set("description", parameter.getDescription()));
                    }
                }

                Maps builder = Maps.of("name", function.getName())
                        .set("description", function.getDescription())
                        .set("parameters", Maps.of("type", "object").set("properties", propertiesMap).set("required", requiredProperties));
                functionsJsonArray.add(builder);
            }

            return functionsJsonArray;
        }
    };


    public static AiMessageParser getAiMessageParser() {
        DefaultAiMessageParser aiMessageParser = new DefaultAiMessageParser() {
            @Override
            public AiMessage parse(JSONObject rootJson) {
                if (!rootJson.containsKey("payload")) {
                    throw new JSONException("json not contains payload: " + rootJson);
                }
                return super.parse(rootJson);
            }
        };
        aiMessageParser.setContentPath("$.payload.choices.text[0].content");
        aiMessageParser.setIndexPath("$.payload.choices.text[0].index");
        aiMessageParser.setCompletionTokensPath("$.payload.usage.text.completion_tokens");
        aiMessageParser.setPromptTokensPath("$.payload.usage.text.prompt_tokens");
        aiMessageParser.setTotalTokensPath("$.payload.usage.text.total_tokens");

        aiMessageParser.setStatusParser(content -> {
            Integer status = (Integer) JSONPath.eval(content, "$.payload.choices.status");
            if (status == null) {
                return MessageStatus.UNKNOW;
            }
            switch (status) {
                case 0:
                    return MessageStatus.START;
                case 1:
                    return MessageStatus.MIDDLE;
                case 2:
                    return MessageStatus.END;
            }
            return MessageStatus.UNKNOW;
        });

        aiMessageParser.setCallsParser(content -> {
            JSONArray toolCalls = (JSONArray) JSONPath.eval(content, "$.payload.choices.text");
            if (toolCalls == null || toolCalls.isEmpty()) {
                return Collections.emptyList();
            }
            List<AiFunctionCall> functionCalls = new ArrayList<>();
            for (int i = 0; i < toolCalls.size(); i++) {
                JSONObject jsonObject = toolCalls.getJSONObject(i);
                JSONObject functionObject = jsonObject.getJSONObject("function_call");
                if (functionObject != null) {
                    AiFunctionCall functionCall = new AiFunctionCall();
                    functionCall.setName(functionObject.getString("name"));
                    Object arguments = functionObject.get("arguments");
                    if (arguments instanceof Map) {
                        //noinspection unchecked
                        functionCall.setArgs((Map<String, Object>) arguments);
                    } else if (arguments instanceof String) {
                        //noinspection unchecked
                        functionCall.setArgs(JSON.parseObject(arguments.toString(), Map.class));
                    }
                    functionCalls.add(functionCall);
                }
            }
            return functionCalls;
        });

        return aiMessageParser;
    }


    public static String promptToPayload(Prompt prompt, SparkLlmConfig config, ChatOptions options) {
        // https://www.xfyun.cn/doc/spark/Web.html#_1-%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
        Maps root = Maps.of("header", Maps.of("app_id", config.getAppId()).set("uid", UUID.randomUUID()));
        root.set("parameter", Maps.of("chat", Maps.of("domain", getDomain(config.getVersion()))
                        .setIf(options.getTemperature() > 0, "temperature", options.getTemperature())
                        .setIf(options.getMaxTokens() != null, "max_tokens", options.getMaxTokens())
                        .setIfNotNull("top_k", options.getTopK())
                )
        );
        root.set("payload", Maps.of("message", Maps.of("text", promptFormat.messagesFormat(prompt)))
                .setIfNotEmpty("functions", Maps.ofNotNull("text", promptFormat.toolsFormat(prompt)))
        );
        return JSON.toJSONString(root);
    }


    public static String createURL(SparkLlmConfig config) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss '+0000'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date());

        String header = "host: spark-api.xf-yun.com\n";
        header += "date: " + date + "\n";
        header += "GET /" + config.getVersion() + "/chat HTTP/1.1";

        String base64 = HashUtil.hmacSHA256ToBase64(header, config.getApiSecret());
        String authorization_origin = "api_key=\"" + config.getApiKey()
                + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + base64 + "\"";

        String authorization = Base64.getEncoder().encodeToString(authorization_origin.getBytes());
        return "ws://spark-api.xf-yun.com/" + config.getVersion() + "/chat?authorization=" + authorization
                + "&date=" + urlEncode(date) + "&host=spark-api.xf-yun.com";
    }

    private static String urlEncode(String content) {
        try {
            return URLEncoder.encode(content, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    private static String getDomain(String version) {
        switch (version) {
            case "v4.0":
                return "4.0Ultra";
            case "v3.5":
                return "generalv3.5";
            case "v3.1":
                return "generalv3";
            case "v2.1":
                return "generalv2";
            case "v1.1":
                return "lite";
            default:
                return "general";
        }
    }

    public static String embedPayload(SparkLlmConfig config, String... documents) {
        String text = Maps.of("messages", Collections.singletonList(Maps.of("content", documents[0]).set("role", "user"))).toJSON();
        String textBase64 = Base64.getEncoder().encodeToString(text.getBytes());

        return Maps.of("header", Maps.of("app_id", config.getAppId()).set("uid", UUID.randomUUID()).set("status", 3))
                .set("parameter", Maps.of("emb", Maps.of("domain", "para")
                        .set("feature", Maps.of("encoding", "utf8").set("compress", "raw").set("format", "plain"))))
                .set("payload", Maps.of("messages", Maps.of("encoding", "utf8").set("compress", "raw")
                        .set("format", "json").set("status", 3).set("text", textBase64)))
                .toJSON();
    }


    ///   http://emb-cn-huabei-1.xf-yun.com/
    public static String createEmbedURL(SparkLlmConfig config) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss '+0000'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date());

        String header = "host: emb-cn-huabei-1.xf-yun.com\n";
        header += "date: " + date + "\n";
        header += "POST / HTTP/1.1";

        String base64 = HashUtil.hmacSHA256ToBase64(header, config.getApiSecret());
        String authorization_origin = "api_key=\"" + config.getApiKey()
                + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + base64 + "\"";

        String authorization = Base64.getEncoder().encodeToString(authorization_origin.getBytes());
        return "http://emb-cn-huabei-1.xf-yun.com/?authorization=" + authorization
                + "&date=" + urlEncode(date) + "&host=emb-cn-huabei-1.xf-yun.com";
    }
}
