package com.uu2.tinyagents.example;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.store.Query;
import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingModel;
import com.uu2.tinyagents.core.memory.DefaultChatMemory;
import com.uu2.tinyagents.core.prompt.FunctionPrompt;
import com.uu2.tinyagents.core.rag.Question;
import com.uu2.tinyagents.core.rag.generation.DefaultGenerator;
import com.uu2.tinyagents.core.rag.graph.ExecuteResult;
import com.uu2.tinyagents.core.rag.graph.Node;
import com.uu2.tinyagents.core.rag.graph.StateGraph;
import com.uu2.tinyagents.core.rag.preretrieval.QueryTranslation;
import com.uu2.tinyagents.core.rag.retrieval.DefaultDocumentRetriever;
import com.uu2.tinyagents.core.document.store.Store;
import com.uu2.tinyagents.llm.ollama.OllamaLlm;
import com.uu2.tinyagents.llm.ollama.OllamaLlmConfig;
import com.uu2.tinyagents.store.milvus.MilvusVectorStore;
import com.uu2.tinyagents.store.milvus.MilvusVectorStoreConfig;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;

import java.util.List;
import java.util.concurrent.Executors;

public class TradeAgents {
    public static void main(String[] args) {
        // 创建 graph
        StateGraph graph = new StateGraph();

        OllamaLlmConfig config = new OllamaLlmConfig();
        config.setEndpoint("http://localhost:11434");
        config.setModel("qwen2.5:14b");
//        config.setModel("deepseek-r1:14b");
        config.setDebug(true);

        Llm llm = new OllamaLlm(config);

        OllamaLlmConfig embeddingConfig = new OllamaLlmConfig();
        embeddingConfig.setEndpoint("http://localhost:11434");
        embeddingConfig.setModel("nomic-embed-text");
        embeddingConfig.setDebug(true);

        EmbeddingModel embeddingModel = new OllamaLlm(embeddingConfig);

        ConnectConfig connCfg = ConnectConfig.builder()
                .uri("http://127.0.0.1:19530")
                .build();
//        Store vectorStore1 = MilvusVectorStore.builder()
//                .embeddingModel(embeddingModel)
//                .client(new MilvusClientV2(connCfg))
//                .config(MilvusVectorStoreConfig.builder()
//                        .collectionName("test")
//                        .build())
//                .build();

        Store testStore = new MockStore();

        Node queryTranslationNode = Node.builder()
                .id("queryTranslation")
                .tasks(List.of(new QueryTranslation(llm)))
                .build();

        Node queryPromptNode = Node.builder()
                .id("queryPrompt")
                .tasks(List.of(ctx -> {

                    String promptTemplate = "You are a helpful assistant. Use the following information to answer the user's question.\n" +
                            "{documents}\n" +
                            "Question: {question}\n" +
                            "Answer:";
                    ctx.getQuestion().setPromptTemplate(promptTemplate);

                    return promptTemplate;
                }))
                .build();

        Node queryRetrievalNode = Node.builder()
                .id("queryRetrieval")
                .tasks(List.of(new DefaultDocumentRetriever(testStore)))
                .build();

        FunctionPrompt prompt = new FunctionPrompt(new DefaultChatMemory());
        Node queryAnswerNode = Node.builder()
                .id("generateAnswer")
                .tasks(List.of(new DefaultGenerator(llm, prompt)))
                .build();

        graph.add(StateGraph.START, queryTranslationNode);
        graph.add(queryTranslationNode, queryPromptNode);
        graph.add(queryPromptNode, queryRetrievalNode);
        graph.add(queryRetrievalNode, queryAnswerNode);

        ExecuteResult result = graph.invoke("今天股票600323行情如何");

        System.out.println(result);
        if (result.getException()!=null) {
            result.getException().printStackTrace();
        }
    }
}
