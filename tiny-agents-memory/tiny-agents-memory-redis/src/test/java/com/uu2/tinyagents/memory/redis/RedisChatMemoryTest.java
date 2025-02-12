package com.uu2.tinyagents.memory.redis;

import com.uu2.tinyagents.core.message.AttachmentMessage;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.SystemMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class RedisChatMemoryTest {

    private RedissonClient redissonClient;

    private final String chatId = "12345";
    private RedisChatMemory redisChatMemory;


    @Before
    public void setUp() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0);

        redissonClient = Redisson.create(config);
        redisChatMemory = new RedisChatMemory(chatId, redissonClient);
        redisChatMemory.clear();

    }

    @After
    public void tearDown() {
        redissonClient.shutdown();
    }

    @Test
    public void addMessage_ValidMessage_MessageAddedToList() {
        Message message = new Message("user", "Hello");

        redisChatMemory.addMessage(message);
        redisChatMemory.addMessage(SystemMessage.of("This is system message"));
        redisChatMemory.addMessage(HumanMessage.of("This is human message"));
        redisChatMemory.addMessage(AttachmentMessage.of("This is attachment message"));

        List<Message> messages = redisChatMemory.getMessages();
        assertEquals(4, messages.size());
        assertEquals(new Message("user", "Hello"), messages.get(0));
        assertEquals(SystemMessage.of("This is system message"), messages.get(1));
        assertEquals(HumanMessage.of("This is human message"), messages.get(2));
        assertEquals(AttachmentMessage.of("This is attachment message"), messages.get(3));
    }

    @Test
    public void addMessage_NullMessage_NoExceptionThrown() {
        redisChatMemory.addMessage(null);
    }

    @Test
    public void getMessages_EmptyList_ReturnsEmptyList() {

        List<Message> messages = redisChatMemory.getMessages();

        assertEquals(0, messages.size());
    }

    @Test
    public void getMessages_MultipleElements_ReturnsAllElements() {
        List<Message> mockMessages = new ArrayList<>();
        mockMessages.add(new Message("user", "Hello"));
        mockMessages.add(new Message("assistant", "Hi there!"));

        redisChatMemory.addMessages(mockMessages);
        List<Message> messages = redisChatMemory.getMessages();

        assertEquals(2, messages.size());
        assertEquals("Hello", messages.get(0).getContent());
        assertEquals("Hi there!", messages.get(1).getContent());
    }

    @Test
    public void clear_ShouldClearTheList() {
        // Arrange
        Message message1 = new Message("user", "Hello");
        Message message2 = new Message("agent", "Hi");

        redisChatMemory.addMessage(message1);
        redisChatMemory.addMessage(message1);

        // Act
        redisChatMemory.clear();

        // Assert
        assertEquals(Collections.emptyList(), redisChatMemory.getList());
    }

    @Test
    public void id_ShouldReturnCorrectChatMemoryKey() {
        String expectedId = "chat:memory:" + chatId;
        assertEquals(expectedId, redisChatMemory.id());
    }

    @Test
    public void addMessages_EmptyCollection_NoException() {
        Collection<Message> messages = Collections.emptyList();
        redisChatMemory.addMessages(messages);
    }

}
