package com.uu2.tinyagents.core.message.tools;

import com.uu2.tinyagents.core.message.Message;
import lombok.Getter;

@Getter
public class ToolExecMessage extends Message {
    private final Object toolCallId;

    public ToolExecMessage(Object toolCallId, Object content) {
        super(Role.TOOL.getRole(), content);
        this.toolCallId = toolCallId;
    }
}
