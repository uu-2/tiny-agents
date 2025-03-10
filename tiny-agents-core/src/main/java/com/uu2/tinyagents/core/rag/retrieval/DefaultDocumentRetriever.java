package com.uu2.tinyagents.core.rag.retrieval;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.store.Query;
import com.uu2.tinyagents.core.document.store.Store;
import com.uu2.tinyagents.core.rag.Question;
import com.uu2.tinyagents.core.rag.graph.ExecuteContext;
import com.uu2.tinyagents.core.rag.graph.Task;

import java.util.ArrayList;
import java.util.List;

public class DefaultDocumentRetriever implements DocumentRetriever, Task {
    protected final Store store;

    public DefaultDocumentRetriever(Store store) {
        this.store = store;
    }

    @Override
    public List<Document> retrieve(Query query) {
        return this.store.search(query);
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
