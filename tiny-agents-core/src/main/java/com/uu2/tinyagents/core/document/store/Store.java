package com.uu2.tinyagents.core.document.store;

import com.uu2.tinyagents.core.llm.embedding.EmbeddingModel;
import com.uu2.tinyagents.core.document.Document;

import java.util.List;

public interface Store {
    List<Document> search(Query query);

    long add(Document document);

    long addAll(List<Document> documents);

    long delete(List<?>ids);

    EmbeddingModel getEmbeddingModel();
}
