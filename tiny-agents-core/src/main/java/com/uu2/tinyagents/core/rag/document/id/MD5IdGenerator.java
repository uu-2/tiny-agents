
package com.uu2.tinyagents.core.rag.document.id;


import com.uu2.tinyagents.core.rag.document.Document;
import com.uu2.tinyagents.core.util.HashUtil;

public class MD5IdGenerator implements DocumentIdGenerator {
    /**
     * Generate a unique ID for the Document
     *
     * @param document Document
     * @return the unique ID
     */
    @Override
    public String  generateId(Document document) {
        return document.getContent() != null ? HashUtil.md5(document.getContent()) : null;
    }

}
