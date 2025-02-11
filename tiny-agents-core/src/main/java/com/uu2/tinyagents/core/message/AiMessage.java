package com.uu2.tinyagents.core.message;

import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AiMessage extends Message {
    private Integer index;
    private MessageStatus status;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private String fullContent;
    // functionName: <argName: argValue>
    private List<AiFunctionCall> calls;

    public AiMessage() {
        super(Role.ASSISTANT.getRole(), "");
    }

    public AiMessage(Object content) {
        super(Role.ASSISTANT.getRole(), content);
    }
}
