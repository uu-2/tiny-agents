
package com.uu2.tinyagents.llm.spark;

import com.uu2.tinyagents.core.llm.BaseLlm;
import com.uu2.tinyagents.core.llm.ChatContext;
import com.uu2.tinyagents.core.llm.ChatOptions;
import com.uu2.tinyagents.core.llm.StreamResponseListener;
import com.uu2.tinyagents.core.llm.client.BaseLlmClientListener;
import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.llm.client.LlmClient;
import com.uu2.tinyagents.core.llm.client.LlmClientListener;
import com.uu2.tinyagents.core.llm.client.impl.WebSocketClient;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingOptions;
import com.uu2.tinyagents.core.llm.response.AbstractMessageResponse;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.llm.response.parser.AiMessageParser;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.util.SleepUtil;
import com.uu2.tinyagents.core.util.StringUtil;
import com.alibaba.fastjson.JSONPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

public class SparkLlm extends BaseLlm<SparkLlmConfig> {

    private static final Logger logger = LoggerFactory.getLogger(SparkLlm.class);
    public AiMessageParser aiMessageParser = SparkLlmUtil.getAiMessageParser();

    private final HttpClient httpClient = new HttpClient();


    public SparkLlm(SparkLlmConfig config) {
        super(config);
    }

    @Override
    public EmbedData embed(EmbeddingOptions options, String... documents) {
        return embed(options, 0, documents);
    }


    public EmbedData embed(EmbeddingOptions options, int tryTimes, String... documents) {
        double[][] vectors = new double[documents.length][];

        for (int docIdx = 0; docIdx < documents.length; docIdx++) {
            String document = documents[docIdx];
            String payload = SparkLlmUtil.embedPayload(config, document);
            String resp = httpClient.post(SparkLlmUtil.createEmbedURL(config), null, payload);
            if (StringUtil.noText(resp)) {
                return null;
            }

            Integer code = JSONPath.read(resp, "$.header.code", Integer.class);
            if (code == null) {
                logger.error(resp);
                return null;
            }

            if (code != 0) {
                //11202	授权错误：秒级流控超限。秒级并发超过授权路数限制
                if (code.equals(11202) && tryTimes < 3) {
                    SleepUtil.sleep(200);
                    return embed(options, tryTimes + 1, documents);
                } else {
                    logger.error(resp);
                    return null;
                }
            }

            String text = JSONPath.read(resp, "$.payload.feature.text", String.class);
            if (StringUtil.noText(text)) {
                logger.error(resp);
                return null;
            }

            byte[] buffer = Base64.getDecoder().decode(text);
            double[] vector = new double[buffer.length / 4];
            for (int i = 0; i < vector.length; i++) {
                int n = i * 4;
                vector[i] = ByteBuffer.wrap(buffer, n, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            }

            vectors[docIdx] = vector;
        }


        EmbedData vectorData = new EmbedData();
        vectorData.setVector(vectors);
        return vectorData;
    }


    @Override
    public AiMessageResponse chat(Prompt prompt, ChatOptions options) {
        CountDownLatch latch = new CountDownLatch(1);
        Throwable[] failureThrowable = new Throwable[1];
        AiMessageResponse[] messageResponse = {null};

        waitResponse(prompt, options, messageResponse, latch, failureThrowable);

        AiMessageResponse response = messageResponse[0];
        Throwable fialureThrowable = failureThrowable[0];

        if (response == null) {
            if (fialureThrowable != null) {
                response = new AiMessageResponse(prompt, "", null);
            } else {
                return null;
            }
        }

        if (fialureThrowable != null || response.getMessage() == null) {
            response.setError(true);
            if (fialureThrowable != null) {
                response.setErrorMessage(fialureThrowable.getMessage());
            }
        } else {
            response.setError(false);
        }

        return response;
    }


    private void waitResponse(Prompt prompt
            , ChatOptions options
            , AbstractMessageResponse<?>[] messageResponse
            , CountDownLatch latch
            , Throwable[] failureThrowable) {

        chatStream(prompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {
                AiMessage message = response.getMessage();
                if (message != null) message.setContent(message.getFullContent());

                messageResponse[0] = response;
            }

            @Override
            public void onStop(ChatContext context) {
                StreamResponseListener.super.onStop(context);
                latch.countDown();
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                logger.error(throwable.toString(), throwable);
                failureThrowable[0] = throwable;
            }
        }, options);

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void chatStream(Prompt prompt, StreamResponseListener listener, ChatOptions options) {
        LlmClient llmClient = new WebSocketClient();
        String url = SparkLlmUtil.createURL(config);
        String payload = SparkLlmUtil.promptToPayload(prompt, config, options);
        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, aiMessageParser);
        llmClient.start(url, null, payload, clientListener, config);
    }


}
