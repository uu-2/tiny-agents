package com.uu2.tinyagents.core.rag.preretrieval;

import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.llm.ollama.OllamaLlm;
import com.uu2.tinyagents.llm.ollama.OllamaLlmConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class QueryTranslationTest {
    OllamaLlm llm;

    @Before
    public void setUp() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("qwen2.5:14b");
//        config.setModel("deepseek-r1:14b");
        config.setDebug(true);

        llm = new OllamaLlm(config);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(new HttpLoggingInterceptor())
                .build();
        llm.setHttpClient(new HttpClient(okHttpClient));
    }

    @Test
    public void invoke() {

        PreRetrieval queryTranslation = new QueryTranslation(llm);
        List<Document> resp = queryTranslation.invoke(Collections.singletonList(Document.of("Python 和 Java 谁更好？")));
        System.out.println(resp);
        assertEquals(5, resp.size());
    }

}