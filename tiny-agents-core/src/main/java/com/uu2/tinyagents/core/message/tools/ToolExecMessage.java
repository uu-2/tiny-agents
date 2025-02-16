package com.uu2.tinyagents.core.message.tools;

import com.alibaba.fastjson2.annotation.JSONField;
import com.uu2.tinyagents.core.message.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolExecMessage extends Message {
    @JSONField(name = "tool_call_id")
    private Object toolCallId;
    private String type = "function";

    public ToolExecMessage(Object toolCallId, Object content) {
        super(Role.TOOL.getRole(), content);
        this.toolCallId = toolCallId;
    }
}
