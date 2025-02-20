
package com.uu2.tinyagents.core.rag.document.id;


import com.uu2.tinyagents.core.rag.document.Document;

public interface DocumentIdGenerator {

    /**
     * Generate a unique ID for the Document
     *
     * @param document Document
     * @return the unique ID
     */
    String generateId(Document document);
}
