package com.uu2.tinyagents.core.memory;

import com.uu2.tinyagents.core.message.Message;

import java.util.Collection;
import java.util.List;

public interface ChatMemory extends Memory {
    List<Message> getMessages();

    void addMessage(Message message);

    default void addMessages(Collection<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        for (Message message : messages) {
            addMessage(message);
        }
    }
}
