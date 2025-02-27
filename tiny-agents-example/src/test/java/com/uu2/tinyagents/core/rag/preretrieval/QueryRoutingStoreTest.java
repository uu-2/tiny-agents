package com.uu2.tinyagents.core.rag.preretrieval;

import com.uu2.tinyagents.core.rag.Question;
import com.uu2.tinyagents.llm.ollama.OllamaLlm;
import com.uu2.tinyagents.llm.ollama.OllamaLlmConfig;
import com.uu2.tinyagents.store.milvus.MilvusVectorStore;
import com.uu2.tinyagents.store.milvus.MilvusVectorStoreConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class QueryRoutingStoreTest {
    OllamaLlm llm;

    @Before
    public void setUp() {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("qwen2.5:14b");
//        config.setModel("deepseek-r1:14b");
        config.setDebug(true);

        llm = new OllamaLlm(config);

    }

    @Test
    public void testInvoke() {

        List<QueryRoutingStore.StoreDef> storeDef = List.of(
                QueryRoutingStore.StoreDef.builder()
                        .store(new MilvusVectorStore(null, null,
                                MilvusVectorStoreConfig.builder().autoCreateCollection(false).build()))
                        .name("python")
                        .description("python知识库，提供 python 的的各类信息，包括但不限于：语法、标准库文档、python语言的优势、性能优化技巧等。")
                        .build(),
                QueryRoutingStore.StoreDef.builder()
                        .store(new MilvusVectorStore(null, null,
                                MilvusVectorStoreConfig.builder().autoCreateCollection(false).build()))
                        .name("javascript")
                        .description("javascript知识库，简称JS知识库。供 js 的的各类信息，包括但不限于：语法、标准库文档、js各类三方库介绍信息、使用技巧等。")
                        .build()
                );
        QueryRoutingStore queryRoutingStore = new QueryRoutingStore(llm, storeDef);

        Question resp = queryRoutingStore.invoke(Question.of("Python 或 JavaScript：哪种编程语言更易于学习和使用？"));
        System.out.println(resp.getStores().get(0));
    }
}
