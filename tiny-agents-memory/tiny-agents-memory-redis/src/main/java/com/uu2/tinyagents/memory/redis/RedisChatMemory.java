package com.uu2.tinyagents.memory.redis;

import com.uu2.tinyagents.core.memory.ChatMemory;
import com.uu2.tinyagents.core.message.Message;
import lombok.Getter;
import org.redisson.api.RedissonClient;
import org.redisson.api.RList;

import java.util.Collection;
import java.util.List;

@Getter
public class RedisChatMemory implements ChatMemory {

    private final RedissonClient redissonClient;

    private final String CHAT_MEMORY_KEY;
    private final RList<Message> list;

    public RedisChatMemory(String id, RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.CHAT_MEMORY_KEY = "chat:memory:" + id;
        this.list = redissonClient.getList(CHAT_MEMORY_KEY);
    }


    @Override
    public List<Message> getMessages() {
        return this.list.readAll();
    }

    @Override
    public List<Message> getMessages(int maxCount) {
        int end = this.list.size();
        int start = Math.max(0, end - maxCount);
        return this.list.subList(start, end);
    }

    @Override
    public void addMessage(Message message) {
        if (message == null) {
            return;
        }
        list.add(message);
    }

    @Override
    public void addMessages(Collection<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        this.list.addAll(messages);
    }

    @Override
    public Object id() {
        return CHAT_MEMORY_KEY;
    }

    @Override
    public void clear() {
        this.list.clear();
    }

}
