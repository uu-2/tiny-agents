package com.uu2.tinyagents.llm.ollama;

import com.alibaba.fastjson.JSON;
import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.exception.LlmException;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.prompt.AttachmentPrompt;
import com.uu2.tinyagents.core.prompt.FunctionPrompt;
import com.uu2.tinyagents.core.tools.ToolExecContext;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.tools.function.Parameter;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.uu2.tinyagents.core.message.AttachmentMessage.*;

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


    @Test
    public void testEmbedding() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("llama3.1");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);
        EmbedData embedData = llm.embed("hello world");
        System.out.println(embedData);
    }


    @Test
    public void testFunctionCall() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("qwen2.5:14b");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);

        FunctionPrompt prompt = new FunctionPrompt("现在是日期和时间多少，并返回现在北京天气情况？", WeatherFunctions.class);
        prompt.addFunctions(List.of(new Function() {
            @Override
            public String getName() {
                return "now_time";
            }

            @Override
            public String getDescription() {
                return "计算当前实时日期和时间,YYYY-MM-DD HH:MM:SS 格式";
            }

            @Override
            public Parameter[] getParameters() {
                return new Parameter[]{
                        Parameter.builder()
                                .name("desc").type("string")
                                .description("需要计算的时间描述。比如前一天、今天、现在等。默认返回当前实时时间")
                                .required(true).build()
                };
            }

            @Override
            public Object invoke(Map<String, Object> argsMap) {
                System.out.println("argsMap = " + JSON.toJSONString(argsMap));
                return new Date().toString();
            }
        }));


        AiMessageResponse response = ToolExecContext.of(prompt).execChat(llm);

        System.out.println(response.getMessage());
    }


    @Test
    public void testVisionModel() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("llava:13b");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);

        AttachmentPrompt imagePrompt = new AttachmentPrompt(new OllamaAttachmentMessage("What's in the picture?"));
        imagePrompt.addAttachments(ImageUrl.builder()
                .imageUrl(new ImageUrl.ImageUrdDef("https://img.shetu66.com/2023/04/25/1682391068745168.png")).build());

        AiMessageResponse response = llm.chat(imagePrompt);
        AiMessage message = response == null ? null : response.getMessage();
        System.out.println(message);
    }

}
