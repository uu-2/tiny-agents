package com.uu2.tinyagents.core.rag.preretrieval;

import com.uu2.tinyagents.core.document.Document;

import java.util.List;

public interface PreRetrieval {

    List<Document> invoke(List<Document> documents);
}
