package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.SystemMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class TextPrompt extends Prompt {
    private SystemMessage systemMessage;
    protected HumanMessage humanMessage;

    public TextPrompt(String content) {
        this.systemMessage = null;
        this.humanMessage = HumanMessage.of(content);
    }

    public TextPrompt(String systemMessage, String content) {
        this.systemMessage = SystemMessage.of(systemMessage);
        this.humanMessage = HumanMessage.of(content);
    }

    public static TextPrompt of(String s) {
        return new TextPrompt(s);
    }


    @Override
    public List<Message> messages(Map<String, Object> params) {
        if (getSystemMessage() != null) {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(getSystemMessage().format(params));
            messages.add(humanMessage);
            return messages;
        }
        return Collections.singletonList(humanMessage.format(params));
    }
}
