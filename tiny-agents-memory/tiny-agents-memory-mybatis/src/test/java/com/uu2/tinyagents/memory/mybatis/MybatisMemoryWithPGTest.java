package com.uu2.tinyagents.memory.mybatis;

import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.SystemMessage;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.message.tools.ToolCallsMessage;
import com.uu2.tinyagents.core.message.tools.ToolExecMessage;
import com.uu2.tinyagents.memory.mybatis.mapper.MessageModalMapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MybatisMemoryWithPGTest {

    MybatisMemory memory;
    SqlSession session;


    @Before
    public void setUp() {
        session = initSqlSessionFactory().openSession();
        memory = new MybatisMemory("test", session.getMapper(MessageModalMapper.class));
    }

    @After
    public void tearDown() {
        memory.clear();
        session.close();
    }

    public static SqlSessionFactory initSqlSessionFactory() {
        DataSource dataSource = dataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("Production", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.setArgNameBasedConstructorAutoMapping(true);
        configuration.addMapper(MessageModalMapper.class);
        configuration.setLogImpl(StdOutImpl.class);
        configuration.setDefaultEnumTypeHandler(org.apache.ibatis.type.EnumOrdinalTypeHandler.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    public static DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tiny-agents?autoReconnect=true&zeroDateTimeBehavior=CONVERT_TO_NULL&useUnicode=true&characterEncoding=utf-8&useSSL=false&nullCatalogMeansCurrent=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&allowPublicKeyRetrieval=true");
        dataSource.setUser("postgres");
        dataSource.setPassword("example");
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT VERSION()");
            resultSet.next();
            System.out.println("=======>  " + resultSet.getString("version"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Test
    public void id_ShouldReturnCorrectId() {
        Object result = memory.id();
        assertEquals("test", result);
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


        memory.addMessages(expectedMessages);
        List<Message> messages = memory.getMessages();

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


        memory.addMessages(expectedMessages);
        memory.addMessage(new ToolExecMessage("1111", "TOOL-OK"));

        List<Message> messages = memory.getMessages(2);

        assertEquals(2, messages.size());
        assertEquals(AiMessage.class, messages.get(0).getClass());
        assertEquals(ToolExecMessage.class, messages.get(1).getClass());
        assertEquals(expectedMessages.get(3).getContent(), messages.get(0).getContent());
        assertEquals(Message.Role.TOOL.getRole(), messages.get(1).getRole());
        assertEquals("TOOL-OK", messages.get(1).getContent());
    }

}