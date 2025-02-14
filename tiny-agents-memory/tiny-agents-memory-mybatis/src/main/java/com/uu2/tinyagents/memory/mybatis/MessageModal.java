package com.uu2.tinyagents.memory.mybatis;

import com.alibaba.fastjson2.JSON;
import com.uu2.tinyagents.core.message.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class MessageModal {
    private Long id;
    private String chatId;
    private String clazz;
    private String message;
    private Date createdAt;

    public Message toMessage() {
        Class clz = null;
        try {
            clz = Class.forName(this.clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return (Message) JSON.parseObject(this.message, clz);
    }

    public MessageModal(String chatId, Message message) {
        this.chatId = chatId;
        this.clazz = message.getClass().getName();
        this.message = JSON.toJSONString(message);
        this.createdAt = new Date();
    }
}
