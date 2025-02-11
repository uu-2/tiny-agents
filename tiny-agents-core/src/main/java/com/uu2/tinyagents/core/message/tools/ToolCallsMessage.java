package com.uu2.tinyagents.core.message.tools;

import com.alibaba.fastjson2.annotation.JSONField;
import com.uu2.tinyagents.core.message.Message;
import lombok.Getter;

import java.util.List;

/**
 * {
 * "role" : "assistant",
 * "content" : "",
 * "tool_calls" : [ {
 * "function" : {
 * "name" : "get_the_weather_info",
 * "arguments" : {
 * "city" : "Beijing"
 * }
 * }
 * } ]
 * }
 */
@Getter
public class ToolCallsMessage extends Message {
    @JSONField(name = "tool_calls")
    private final List<AiFunctionCall> toolCalls;

    public ToolCallsMessage(List<AiFunctionCall> calls) {
        super(Role.ASSISTANT.getRole(), null);
        this.toolCalls = calls;
    }

}
