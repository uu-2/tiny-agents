package com.uu2.tinyagents.core.rag.retrieval;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.store.Query;

import java.util.List;
import java.util.function.Function;

public interface DocumentRetriever extends Function<Query, List<Document>> {

    default List<Document> apply(Query query) {
        return this.retrieve(query);
    }

    List<Document> retrieve(Query query);
}
