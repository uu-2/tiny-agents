
package com.uu2.tinyagents.core.memory;


import com.uu2.tinyagents.core.message.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DefaultChatMemory implements ChatMemory {
    private final Object id;
    private final List<Message> messages = new ArrayList<>();

    public DefaultChatMemory() {
        this.id = UUID.randomUUID().toString();
    }

    public DefaultChatMemory(Object id) {
        this.id = id;
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void clear() {
        messages.clear();
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<Message> getMessages(int maxCount) {
        int total = this.messages.size();
        int start = Math.max(0, total - maxCount);
        return this.messages.subList(start, total);
    }

    @Override
    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void addMessages(Collection<Message> messages) {
        this.messages.addAll(messages);
    }


}
