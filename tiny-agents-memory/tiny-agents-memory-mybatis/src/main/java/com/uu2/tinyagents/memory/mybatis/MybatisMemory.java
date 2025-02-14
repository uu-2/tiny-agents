package com.uu2.tinyagents.memory.mybatis;

import com.uu2.tinyagents.core.memory.ChatMemory;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.memory.mybatis.mapper.MessageModalMapper;

import java.util.Comparator;
import java.util.List;

public class MybatisMemory implements ChatMemory {

    private final String chatId;
    private final MessageModalMapper mapper;

    public MybatisMemory(String chatId, MessageModalMapper mapper) {
        this.chatId = chatId;
        this.mapper = mapper;
    }

    @Override
    public List<Message> getMessages() {
        List<MessageModal> msgData = mapper.selectList(chatId);
        return msgData.stream().map(MessageModal::toMessage).toList();
    }

    @Override
    public List<Message> getMessages(int maxCount) {
        List<MessageModal> msgList = mapper.selectPage(maxCount, chatId);
        return msgList.stream()
                .sorted(Comparator.comparing(MessageModal::getId))
                .map(MessageModal::toMessage)
                .toList();
    }

    @Override
    public void addMessage(Message message) {
        mapper.insert(new MessageModal(chatId, message));
    }

    @Override
    public Object id() {
        return chatId;
    }

    @Override
    public void clear() {
        mapper.deleteByChatId(chatId);
    }
}
