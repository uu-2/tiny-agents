package com.uu2.tinyagents.memory.mongo;

import com.alibaba.fastjson2.JSON;
import com.uu2.tinyagents.core.message.Message;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.io.Serial;
import java.io.Serializable;

public class MessageCodec implements Codec<MessageModel> {

    @Override
    public MessageModel decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        ObjectId id = bsonReader.readObjectId();
        String className = bsonReader.readString("class");
        String msg = bsonReader.readString("msg");

        bsonReader.readEndDocument();

        Class clz = null;
        try {
            clz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Message message = (Message) JSON.parseObject(msg, clz);
        return new MessageModel(id, message);
    }

    @Override
    public void encode(BsonWriter bsonWriter, MessageModel t, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();
        bsonWriter.writeString("class", t.getMessage().getClass().getName());
        bsonWriter.writeString("msg", JSON.toJSONString(t.getMessage()));
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<MessageModel> getEncoderClass() {
        return MessageModel.class;
    }
}
