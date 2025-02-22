package com.uu2.tinyagents.core.rag.preretrieval;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.rag.store.Query;
import com.uu2.tinyagents.core.rag.store.Store;

import java.util.List;

public class QueryRoutingPrompt implements PreRetrieval {

    private Store promptStore;

    public QueryRoutingPrompt(Store promptStore) {
        this.promptStore = promptStore;
    }

    @Override
    public List<Document> invoke(List<Document> documents) {
        if (documents.isEmpty()) {
            return List.of();
        }

        String text = documents.stream().map(Document::getContent).reduce((a, b) -> a + "\n" + b).get();
        return this.promptStore.search(Query.of(text));
    }
}
