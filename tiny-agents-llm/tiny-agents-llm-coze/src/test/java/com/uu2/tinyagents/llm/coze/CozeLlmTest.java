package com.uu2.tinyagents.llm.coze;

import com.uu2.tinyagents.core.llm.ChatContext;
import com.uu2.tinyagents.core.llm.StreamResponseListener;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.prompt.TextPrompt;
import junit.framework.TestCase;
import org.junit.Test;

public class CozeLlmTest extends TestCase {
    String token = "<your token>";
    String botId = "<your bot-id>";
    String userId = "your-id";
    String textPrompt = "你是谁？告诉我你能干什么事情？并列出5点你能做的事情";
    CozeLlmConfig config;
    CozeLlm llm;
    CozeChatOptions options;

    public CozeLlmTest() {
        config = new CozeLlmConfig();
        config.setApiKey(token);
        config.setDebug(true);

        llm  = new CozeLlm(config);
        options = new CozeChatOptions();
        options.setBotId(botId);
        options.setUserId(userId);
    }


    @Test
    public void testChat() {
        TextPrompt prompt = new TextPrompt(textPrompt);
        AiMessageResponse response = llm.chat(prompt, options);

        if (response.isError()){
            System.out.println(response.getErrorMessage());
            return;
        }


        AiMessage message = response.getMessage();
        String content = String.valueOf(message.getContent());
        System.out.println(content);
    }

    @Test
    public void testChatStream() {
        TextPrompt prompt = new TextPrompt(textPrompt);
        llm.chatStream(prompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {
                AiMessage message = response.getMessage();
                System.out.print(message.getContent());
            }

            @Override
            public void onStart(ChatContext context) {
                StreamResponseListener.super.onStart(context);
            }

            @Override
            public void onStop(ChatContext context) {
                // stop 后才能拿到 token 用量等信息
                CozeChatContext ccc = (CozeChatContext) context;
                System.out.println(ccc.getUsage());
                StreamResponseListener.super.onStop(context);
            }
        }, options);
    }

}