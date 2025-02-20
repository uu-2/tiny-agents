package com.uu2.tinyagents.store.milvus;

import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingModel;
import com.uu2.tinyagents.core.rag.document.Document;
import com.uu2.tinyagents.core.rag.document.id.MD5IdGenerator;
import com.uu2.tinyagents.core.rag.document.id.RandomIdGenerator;
import com.uu2.tinyagents.core.rag.store.Query;
import com.uu2.tinyagents.core.util.Maps;
import com.uu2.tinyagents.llm.ollama.OllamaLlm;
import com.uu2.tinyagents.llm.ollama.OllamaLlmConfig;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.collection.request.DropCollectionReq;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MilvusVectorStoreTest {

    EmbeddingModel embeddingModel;
    MilvusVectorStore vectorStore;
    RandomIdGenerator idGenerator = new RandomIdGenerator();

    @Before
    public void setUp() throws Exception {
        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("nomic-embed-text");
        config.setDebug(true);

        embeddingModel = new OllamaLlm(config);

        ConnectConfig connCfg = ConnectConfig.builder()
                .uri("http://127.0.0.1:19530")
                .build();
        vectorStore = MilvusVectorStore.builder()
                .embeddingModel(embeddingModel)
                .client(new MilvusClientV2(connCfg))
                .config(MilvusVectorStoreConfig.builder()
                        .collectionName("test")
                        .build())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        new MilvusClientV2(ConnectConfig.builder()
                .uri("http://localhost:19530")
                .build())
                .dropCollection(DropCollectionReq.builder()
                        .collectionName("test")
                        .build());
    }

    @Test
    public void search() {
        Document doc = Document.builder()
                .content("test")
                .id(idGenerator.generateId(null))
                .metadataMap(Map.of(
                        "source", "test",
                        "length", 5
                ))
                .build();

        vectorStore.add(doc);

        List<Document> list = vectorStore.search(Query.builder()
                .text("test")
                .build());

        assertEquals(1, list.size());
    }
}