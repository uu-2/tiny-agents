package com.uu2.tinyagents.core.rag.preretrieval;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.rag.Question;
import com.uu2.tinyagents.core.document.store.Query;
import com.uu2.tinyagents.core.document.store.Store;

import java.util.List;

public class QueryRoutingPrompt implements PreRetrieval {

    private Store promptStore;

    public QueryRoutingPrompt(Store promptStore) {
        this.promptStore = promptStore;
    }

    @Override
    public Question invoke(Question query) {
        List<Document> prompt = promptStore.search(Query.builder()
                .topK(1)
                .text(query.getText())
                .build());
        if (!prompt.isEmpty()) {
            query.setPromptTemplate(prompt.get(0).getContent());
        }

        return query;
    }
}
