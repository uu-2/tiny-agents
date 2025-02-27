package com.uu2.tinyagents.core.rag.retrieval;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.store.Query;
import com.uu2.tinyagents.core.document.store.Store;
import com.uu2.tinyagents.core.rag.Question;
import com.uu2.tinyagents.core.rag.graph.ExecuteContext;
import com.uu2.tinyagents.core.rag.graph.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RelationDocumentRetriever implements DocumentRetriever, Task {
    private final DocumentRetriever retriever;
    private final Store store;
    private final int width;

    public RelationDocumentRetriever(DocumentRetriever retriever, Store store, int width) {
        this.retriever = retriever;
        this.store = store;
        this.width = width;
    }

    @Override
    public List<Document> retrieve(Query query) {
        return this.retrieve(store, query);
    }

    public List<Document> retrieve(Store store, Query query) {
        List<Document> docChunks = retriever.retrieve(query);

        if (docChunks.isEmpty()) {
            return docChunks;
        }

        docChunks.sort(Comparator.comparing(Document::getSource));

        // TODO 同源文档宽度合并

        List<Document> documents = store.search(Query.builder()
                .topK(query.getTopK())
                .outputVector(query.isOutputVector())
                .filter(Map.of(
                        "source", docChunks.get(0).getMetadata("source")
                ))
                .build());

        return documents;
    }

    @Override
    public Object invoke(ExecuteContext ctx) {
        Question question = ctx.getQuestion();
        List<Store> stores = question.getStores();
        if (stores == null || stores.isEmpty()) {
            return this.retrieve(Query.of(question.getText()));
        }

        List<Document> allDocuments = new ArrayList<>();

        for (Store store : stores) {
            List<Document> docList = store.search(Query.of(question.getText()));
            allDocuments.addAll(docList);
        }
        question.setDocuments(allDocuments);
        return allDocuments;
    }
}
