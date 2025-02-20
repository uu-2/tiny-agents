package com.uu2.tinyagents.core.rag.retrieval;

import com.uu2.tinyagents.core.rag.document.Document;
import com.uu2.tinyagents.core.rag.store.Query;
import com.uu2.tinyagents.core.rag.store.Store;

import java.util.List;

public class DefaultDocumentRetriever implements DocumentRetriever{
    private final Store store;

    public DefaultDocumentRetriever(Store store) {
        this.store = store;
    }

    @Override
    public List<Document> retrieve(Query query) {
        return this.store.search(query);
    }
}
