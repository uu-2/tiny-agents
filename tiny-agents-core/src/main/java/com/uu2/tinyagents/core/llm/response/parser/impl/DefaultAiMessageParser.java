
package com.uu2.tinyagents.core.llm.response.parser.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.llm.response.parser.JSONObjectParser;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.message.MessageStatus;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DefaultAiMessageParser implements AiMessageParser {

    private String contentPath;
    private String indexPath;
    private String totalTokensPath;
    private String promptTokensPath;
    private String completionTokensPath;
    private JSONObjectParser<MessageStatus> statusParser;
    private JSONObjectParser<List<AiFunctionCall>> callsParser;

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public String getTotalTokensPath() {
        return totalTokensPath;
    }

    public void setTotalTokensPath(String totalTokensPath) {
        this.totalTokensPath = totalTokensPath;
    }

    public String getPromptTokensPath() {
        return promptTokensPath;
    }

    public void setPromptTokensPath(String promptTokensPath) {
        this.promptTokensPath = promptTokensPath;
    }

    public String getCompletionTokensPath() {
        return completionTokensPath;
    }

    public void setCompletionTokensPath(String completionTokensPath) {
        this.completionTokensPath = completionTokensPath;
    }

    public JSONObjectParser<MessageStatus> getStatusParser() {
        return statusParser;
    }

    public void setStatusParser(JSONObjectParser<MessageStatus> statusParser) {
        this.statusParser = statusParser;
    }

    public JSONObjectParser<List<AiFunctionCall>> getCallsParser() {
        return callsParser;
    }

    public void setCallsParser(JSONObjectParser<List<AiFunctionCall>> callsParser) {
        this.callsParser = callsParser;
    }

    @Override
    public AiMessage parse(JSONObject rootJson) {
        AiMessage aiMessage = new AiMessage();

        if (StringUtil.hasText(this.contentPath)) {
            aiMessage.setContent((String) JSONPath.eval(rootJson, this.contentPath));
        }

        if (StringUtil.hasText(this.indexPath)) {
            aiMessage.setIndex((Integer) JSONPath.eval(rootJson, this.indexPath));
        }


        if (StringUtil.hasText(promptTokensPath)) {
            aiMessage.setPromptTokens((Integer) JSONPath.eval(rootJson, this.promptTokensPath));
        }

        if (StringUtil.hasText(completionTokensPath)) {
            aiMessage.setCompletionTokens((Integer) JSONPath.eval(rootJson, this.completionTokensPath));
        }

        if (StringUtil.hasText(this.totalTokensPath)) {
            aiMessage.setTotalTokens((Integer) JSONPath.eval(rootJson, this.totalTokensPath));
        }
        //some LLMs like Ollama not response the total tokens
        else if (aiMessage.getPromptTokens() != null && aiMessage.getCompletionTokens() != null) {
            aiMessage.setTotalTokens(aiMessage.getPromptTokens() + aiMessage.getCompletionTokens());
        }

        if (this.statusParser != null) {
            aiMessage.setStatus(this.statusParser.parse(rootJson));
        }

        if (callsParser != null) {
            aiMessage.setCalls(callsParser.parse(rootJson));
        }

        return aiMessage;
    }


    public static DefaultAiMessageParser getChatGPTMessageParser(boolean isStream) {
        DefaultAiMessageParser aiMessageParser = new DefaultAiMessageParser();
        if (isStream) {
            aiMessageParser.setContentPath("$.choices[0].delta.content");
        } else {
            aiMessageParser.setContentPath("$.choices[0].message.content");
        }

        aiMessageParser.setIndexPath("$.choices[0].index");
        aiMessageParser.setTotalTokensPath("$.usage.total_tokens");
        aiMessageParser.setPromptTokensPath("$.usage.prompt_tokens");
        aiMessageParser.setCompletionTokensPath("$.usage.completion_tokens");

        aiMessageParser.setStatusParser(content -> {
            Object finishReason = JSONPath.eval(content, "$.choices[0].finish_reason");
            if (finishReason != null) {
                return MessageStatus.END;
            }
            return MessageStatus.MIDDLE;
        });

        aiMessageParser.setCallsParser(content -> {
            JSONArray toolCalls = (JSONArray) JSONPath.eval(content, "$.choices[0].message.tool_calls");
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
                    aiFunctionCalls.add(aiFunctionCall);
                }
            }
            return aiFunctionCalls;
        });

        return aiMessageParser;
    }
}
