
package com.uu2.tinyagents.core.rag.document.id;


import com.uu2.tinyagents.core.rag.document.Document;

import java.util.UUID;

public class RandomIdGenerator implements DocumentIdGenerator {

    /**
     * Generate a unique ID for the Document
     *
     * @param document Document
     * @return the unique ID
     */
    @Override
    public String generateId(Document document) {
        return UUID.randomUUID().toString();
    }
}
