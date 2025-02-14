package com.uu2.tinyagents.memory.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.uu2.tinyagents.core.memory.ChatMemory;
import com.uu2.tinyagents.core.message.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Sorts.descending;
import static org.bson.codecs.configuration.CodecRegistries.*;

public class MongoChatMemory implements ChatMemory {
    private final String id;
    private final MongoCollection<MessageModel> collection;

    public MongoChatMemory(String id, MongoDatabase mongoDB) {
        this.id = id;
        String CHAT_MEMORY_COLLECTION = "chat_memory_";
        mongoDB = mongoDB.withCodecRegistry(fromCodecs(new MessageCodec()));
        this.collection = mongoDB.getCollection(CHAT_MEMORY_COLLECTION + id, MessageModel.class);
    }

    @Override
    public List<Message> getMessages() {
        List<MessageModel> res = this.collection.find().into(new ArrayList<>());
        return res.stream().map(MessageModel::getMessage).toList();
    }

    @Override
    public List<Message> getMessages(int maxCount) {
        List<MessageModel> msg = this.collection.find()
                .sort(descending("_id"))
                .limit(maxCount)
                .into(new ArrayList<>());
        Collections.reverse(msg);
        return msg.stream().map(MessageModel::getMessage).toList();
    }

    @Override
    public void addMessage(Message message) {
        this.collection.insertOne(new MessageModel(message));
    }

    @Override
    public void addMessages(Collection<Message> messages) {
        this.collection.insertMany(messages.stream().map(MessageModel::new).toList());
    }

    @Override
    public Object id() {
        return this.id;
    }

    @Override
    public void clear() {
        this.collection.drop();
    }

}
