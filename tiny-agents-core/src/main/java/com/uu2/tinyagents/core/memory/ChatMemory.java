package com.uu2.tinyagents.core.memory;

import com.uu2.tinyagents.core.message.Message;

import java.util.Collection;
import java.util.List;

public interface ChatMemory extends Memory {
    List<Message> getMessages();

    void addMessage(Message message);

    default void addMessages(Collection<Message> messages){
        for (Message message : messages) {
            addMessage(message);
        }
    }
}
