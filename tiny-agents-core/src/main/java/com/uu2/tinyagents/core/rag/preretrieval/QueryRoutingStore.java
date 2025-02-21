package com.uu2.tinyagents.core.rag.preretrieval;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.embedding.EmbedData;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.prompt.TextPrompt;
import com.uu2.tinyagents.core.rag.document.Document;
import com.uu2.tinyagents.core.rag.store.Query;
import com.uu2.tinyagents.core.rag.store.Store;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryRoutingStore implements PreRetrieval {


    private final Llm llm;
    private final List<StoreDef> storeDefs;

    public QueryRoutingStore(Llm llm, List<StoreDef> storeDefs) {
        this.llm = llm;
        this.storeDefs = storeDefs;
    }

    @Override
    public List<Document> invoke(List<Document> documents) {

        TextPrompt prompt = TextPrompt.of("""
                你是一个AI语言模型助手。你的任务是根据用户的原始问题，从给定的 STORE_JSON 中选择正确的 STORE，并返回对应 name。
                通过生成用户问题的多个视角，你的目标是帮助用户选择能解决改问题所需的信息知识库。如果不能确定就第一个知识库名字。
                请提供这些由换行符分隔的问题的知识库。以JSON数组的格式格式返回，请参考 RESULT_FORMAT。
                STORE_JSON：
                {store_List}
                
                RESULT_FORMAT：
                ["nameA","nameB"]
                
                原始问题是：
                {question}
                
                请记住，你只需要返回包含知识库名字的JSON结构，不需要其他辅助信息。
                """);

        AiMessageResponse resp = prompt.invoke(llm,
                Map.of("store_List", JSON.toJSONString(storeDefs),
                        "question", documents));
        List<String> storeNames = JSON.parseObject(resp.getMessage().getContent().toString(), ArrayList.class);

        Map<String, StoreDef> storeDefMap = storeDefs.stream().collect(
                Collectors.toMap(x -> x.getName(), x -> x));

        Map<String, Store> storeMap = storeNames.stream().collect(
                Collectors.toMap(x -> x, x -> storeDefMap.get(x).getStore()));

        return documents;
    }

    @Data
    @Builder
    public static class StoreDef {
        @JSONField(serialize = false)
        private Store store;
        private String name;
        private String description;
    }
}
