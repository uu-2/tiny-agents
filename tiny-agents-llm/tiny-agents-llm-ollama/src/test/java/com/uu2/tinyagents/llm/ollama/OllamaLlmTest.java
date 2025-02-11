package com.uu2.tinyagents.llm.ollama;

import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.exception.LlmException;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.prompt.FunctionPrompt;
import com.uu2.tinyagents.core.prompt.TextPrompt;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.LogManager;

public class OllamaLlmTest {

    @Test
    public void testChatOK() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("llama3.2");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);
        String chat = llm.chat("who are your");
        System.out.println(chat);
        assert chat != null;
    }

    @Test(expected = LlmException.class)
    public void testChat() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("llama3");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);
        String chat = llm.chat("who are your");
        System.out.println(chat);
    }


    @Test
    public void testChatStream() throws InterruptedException {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("llama3.2");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);
        llm.chatStream("who are your", (context, response) -> System.out.println(response.getMessage().getContent()));

        Thread.sleep(20000);
    }

//
//
//    @Test
//    public void testEmbedding() {
//        OllamaLlmConfig config = new OllamaLlmConfig();
//        config.setEndpoint("http://localhost:11434");
//        config.setModel("llama3.1");
//        config.setDebug(true);
//
//        Llm llm = new OllamaLlm(config);
//        VectorData vectorData = llm.embed(Document.of("hello world"));
//        System.out.println(vectorData);
//    }
//
//
    @Test
    public void testFunctionCall() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("llama3.2");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);

        FunctionPrompt prompt = new FunctionPrompt("What's the weather like in Beijing?", WeatherFunctions.class);
        AiMessageResponse response = llm.chat(prompt);

        prompt.processAssistantMessage(response.getMessage());
        while (prompt.isHasTools()) {
            response = llm.chat(prompt);
            prompt.processAssistantMessage(response.getMessage());
        }

        System.out.println(response.getMessage());
    }
//
//
//    @Test
//    public void testVisionModel() {
//        OllamaLlmConfig config = new OllamaLlmConfig();
//        config.setEndpoint("http://localhost:11434");
//        config.setModel("llama3.2");
//        config.setDebug(true);
//
//        Llm llm = new OllamaLlm(config);
//
//        TextPrompt prompt = TextPrompt.of("What's the weather like in Beijing?");
//
//        AiMessageResponse response = llm.chat(prompt);
//        AiMessage message = response == null ? null : response.getMessage();
//        System.out.println(message);
//    }

}
