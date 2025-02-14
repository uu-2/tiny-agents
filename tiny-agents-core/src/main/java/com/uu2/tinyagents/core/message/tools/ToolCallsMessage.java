package com.uu2.tinyagents.core.message.tools;

import com.alibaba.fastjson2.annotation.JSONField;
import com.uu2.tinyagents.core.message.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
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
@Setter
public class ToolCallsMessage extends Message {
    @JSONField(name = "tool_calls")
    private List<AiFunctionCall> toolCalls;

    public ToolCallsMessage(Object content, List<AiFunctionCall> calls) {
        super(Role.ASSISTANT.getRole(), content);
        this.toolCalls = calls;
    }

}
