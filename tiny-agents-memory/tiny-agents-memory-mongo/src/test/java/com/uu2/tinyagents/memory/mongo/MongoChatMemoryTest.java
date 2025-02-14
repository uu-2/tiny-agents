package com.uu2.tinyagents.memory.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.SystemMessage;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.message.tools.ToolCallsMessage;
import com.uu2.tinyagents.core.message.tools.ToolExecMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MongoChatMemoryTest {

    @InjectMocks
    private MongoChatMemory mongoChatMemory;

    @Before
    public void setUp() {
        // 连接到 mongodb 服务

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder -> builder.maxWaitTime(60, TimeUnit.SECONDS))
                .applyToClusterSettings(builder -> builder.serverSelectionTimeout(60, TimeUnit.SECONDS))
                .applyToSocketSettings(builder -> builder.connectTimeout(60, TimeUnit.SECONDS))
                .applyToSslSettings(builder -> builder.enabled(false))
                .applyConnectionString(new ConnectionString("mongodb://root:example@localhost:27017"))
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("tiny-agents");

        mongoChatMemory = new MongoChatMemory("test-id", mongoDatabase);

    }

    @After
    public void tearDown() {
        // 关闭连接
        mongoChatMemory.clear();
    }

    @Test
    public void id_ShouldReturnCorrectId() {
        Object result = mongoChatMemory.id();
        assertEquals("test-id", result);
    }


    @Test
    public void getMessages_MultipleMessages_ReturnsAllMessages() {
        ArrayList<Message> expectedMessages = new ArrayList<>();
        expectedMessages.add(SystemMessage.of("system"));
        expectedMessages.add(HumanMessage.of("Hello"));
        AiFunctionCall call = new AiFunctionCall();
        call.setName("test");
        call.setArgs(Map.of("123", "234"));
        call.setCallId("asdfa");
        ToolCallsMessage toolCall = new ToolCallsMessage("Tool", List.of(call));
        expectedMessages.add(toolCall);
        expectedMessages.add(new AiMessage("okok"));


        mongoChatMemory.addMessages(expectedMessages);
        List<Message> messages = mongoChatMemory.getMessages();

        assertEquals(4, messages.size());
        assertEquals(ToolCallsMessage.class, messages.get(2).getClass());
        assertEquals(AiMessage.class, messages.get(3).getClass());
        assertEquals(expectedMessages.get(0).getContent(), messages.get(0).getContent());
        assertEquals(expectedMessages.get(1).getRole(), messages.get(1).getRole());

        assertEquals(expectedMessages.get(2).getContent(), messages.get(2).getContent());
        ToolCallsMessage tmsg = (ToolCallsMessage) messages.get(2);
        assertEquals(toolCall.getToolCalls().get(0).getName(), tmsg.getToolCalls().get(0).getName());
    }

    @Test
    public void testGetMessages() {

        ArrayList<Message> expectedMessages = new ArrayList<>();
        expectedMessages.add(SystemMessage.of("system"));
        expectedMessages.add(HumanMessage.of("Hello"));
        AiFunctionCall call = new AiFunctionCall();
        call.setName("test");
        call.setArgs(Map.of("123", "234"));
        call.setCallId("1111");
        ToolCallsMessage toolCall = new ToolCallsMessage("Tool", List.of(call));
        expectedMessages.add(toolCall);
        expectedMessages.add(new AiMessage("okok"));


        mongoChatMemory.addMessages(expectedMessages);
        mongoChatMemory.addMessage(new ToolExecMessage("1111", "TOOL-OK"));

        List<Message> messages = mongoChatMemory.getMessages(2);

        assertEquals(2, messages.size());
        assertEquals(expectedMessages.get(3).getContent(), messages.get(0).getContent());
        assertEquals(Message.Role.TOOL.getRole(), messages.get(1).getRole());
        assertEquals("TOOL-OK", messages.get(1).getContent());
    }
}
