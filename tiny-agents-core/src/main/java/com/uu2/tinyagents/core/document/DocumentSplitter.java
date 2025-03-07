package com.uu2.tinyagents.core.document;

import com.uu2.tinyagents.core.document.id.DocumentIdGenerator;

import java.util.List;

public interface DocumentSplitter {
    List<Document> split(Document document, DocumentIdGenerator idGenerator);
}
