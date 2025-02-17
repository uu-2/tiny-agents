package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.memory.ChatMemory;
import com.uu2.tinyagents.core.memory.DefaultChatMemory;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.SystemMessage;
import com.uu2.tinyagents.core.util.ArrayUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class HistoriesPrompt extends TextPrompt {
    protected ChatMemory memory;

    private int maxAttachedMessageCount = 10;

    public HistoriesPrompt(ChatMemory memory) {
        super(null);
        this.memory = memory;
    }

    @Override
    public List<Message> messages(Map<String, Object> params) {
        List<Message> messageList = memory.getMessages();
        if (messageList == null) messageList = new ArrayList<>();

        if (messageList.size() > maxAttachedMessageCount) {
            messageList = messageList.subList(messageList.size() - maxAttachedMessageCount, messageList.size());
        }

        List<Message> result = new LinkedList<>();

        if (getSystemMessage() != null) {
            result.add(getSystemMessage().format(params));
        }
        if (!messageList.isEmpty()) {
            result.addAll(messageList.stream().map(m -> m.format(params)).toList());
        }

        return result;
    }

    public void processAssistantMessage(AiMessage lastAiMessage) {
        super.processAssistantMessage(lastAiMessage);
        memory.addMessage(lastAiMessage);
    }

    public void addMessage(List<Message> execMessages) {
        this.memory.addMessages(execMessages);
    }


    public void addMessage(Message message) {
        this.memory.addMessage(message);
    }

    public void clear() {
        memory.clear();
    }
}
