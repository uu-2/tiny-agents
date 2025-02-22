
package com.uu2.tinyagents.core.document.id;


import com.uu2.tinyagents.core.document.Document;

import java.util.UUID;

public class RandomIdGenerator implements DocumentIdGenerator {

    public static String id() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a unique ID for the Document
     *
     * @param document Document
     * @return the unique ID
     */
    @Override
    public String generateId(Document document) {
        return id();
    }
}
