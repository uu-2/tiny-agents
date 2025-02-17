package com.uu2.tinyagents.llm.qwen;

import com.alibaba.fastjson2.JSON;
import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.exception.LlmException;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.AttachmentMessage;
import com.uu2.tinyagents.core.prompt.AttachmentPrompt;
import com.uu2.tinyagents.core.prompt.FunctionPrompt;
import com.uu2.tinyagents.core.tools.ToolExecContext;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.tools.function.Parameter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class QwenLlmTest {
    Llm llm;
    // @see https://www.bigmodel.cn/usercenter/proj-mgmt/apikeys
    private final String  apiKey = "<your apikey>";

    @Before
    public void init() {
        QwenLlmConfig config = new QwenLlmConfig();
        config.setApiKey(apiKey);
        config.setDebug(true);

        llm = new QwenLlm(config);
    }

    @Test
    public void testChatOK() {

        String chat = llm.chat("who are your");
        System.out.println(chat);
        assert chat != null;
    }

    @Test(expected = LlmException.class)
    public void testChat() {

        QwenLlmConfig config = new QwenLlmConfig();
        config.setApiKey(apiKey);
        config.setModel("llama3");
        config.setDebug(true);

        llm = new QwenLlm(config);

        String chat = llm.chat("who are your");
        System.out.println(chat);
    }


    @Test
    public void testChatStream() throws InterruptedException {

        llm.chatStream("who are your", (context, response) -> System.out.println(response.getMessage().getContent()));

        Thread.sleep(6 * 1000);
    }


    @Test
    public void testEmbedding() {

        EmbedData embedData = llm.embed("hello world");
        System.out.println(JSON.toJSON(embedData));

        assert embedData != null;
        assert embedData.getVector() != null;
        assert embedData.getVector().length == 1;

        embedData = llm.embed("hello", "world");
        System.out.println(JSON.toJSON(embedData));

        assert embedData != null;
        assert embedData.getVector() != null;
        assert embedData.getVector().length == 2;
    }


    @Test
    public void testFunctionCall() {

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
                return "2025-2-11 22:36:12";
            }
        }));


        AiMessageResponse response = ToolExecContext.of(prompt).execChat(llm);

        System.out.println(response.getMessage());
    }


    @Test
    public void testVisionModel() {

        QwenLlmConfig config = new QwenLlmConfig();
        config.setApiKey(apiKey);
        config.setModel("glm-4v-plus");
        config.setDebug(true);

        llm = new QwenLlm(config);

        AttachmentPrompt imagePrompt = new AttachmentPrompt(new AttachmentMessage("What's in the picture?"));
        imagePrompt.addAttachments(AttachmentMessage.ImageUrl.builder()
                .imageUrl(new AttachmentMessage.ImageUrl.ImageUrdDef("https://img.shetu66.com/2023/04/25/1682391068745168.png")).build());

        AiMessageResponse response = llm.chat(imagePrompt);
        AiMessage message = response == null ? null : response.getMessage();
        System.out.println(message);
    }

}
