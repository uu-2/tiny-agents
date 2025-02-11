package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.SystemMessage;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
public class TextPrompt extends Prompt{
    private SystemMessage systemMessage;
    protected HumanMessage humanMessage;

    public TextPrompt(String content)
    {
        this.systemMessage = null;
        this.humanMessage = HumanMessage.of(content);
    }

    public TextPrompt(String systemMessage, String content)
    {
        this.systemMessage = SystemMessage.of(systemMessage);
        this.humanMessage = HumanMessage.of(content);
    }

    public static TextPrompt of(String s) {
        return new TextPrompt(s);
    }

    @Override
    public List<Message> messages() {
        if (systemMessage != null) {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(humanMessage);
            return messages;
        }
        return Collections.singletonList(humanMessage);
    }
}
