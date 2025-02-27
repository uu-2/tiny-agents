package com.uu2.tinyagents.example;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.store.Query;
import com.uu2.tinyagents.core.document.store.Store;
import com.uu2.tinyagents.core.llm.embedding.EmbeddingModel;

import java.util.List;

public class MockStore implements Store {
    @Override
    public List<Document> search(Query query) {
        return List.of();
    }

    @Override
    public long add(Document document) {
        return 0;
    }

    @Override
    public long addAll(List<Document> documents) {
        return 0;
    }

    @Override
    public long delete(List<?> ids) {
        return 0;
    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        return null;
    }
}
