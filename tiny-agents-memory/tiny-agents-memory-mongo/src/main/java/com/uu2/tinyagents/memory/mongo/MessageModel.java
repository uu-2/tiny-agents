package com.uu2.tinyagents.memory.mongo;

import com.uu2.tinyagents.core.message.Message;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class MessageModel {
    private ObjectId _id;
    private Message message;

    public MessageModel(Message message) {
        this.message = message;
    }

    public MessageModel(ObjectId id, Message message) {
        this._id = id;
        this.message = message;
    }

}
