
package com.uu2.tinyagents.core.document.splitter;


import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.DocumentSplitter;
import com.uu2.tinyagents.core.document.id.DocumentIdGenerator;
import com.uu2.tinyagents.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class SimpleDocumentSplitter implements DocumentSplitter {
    private int chunkSize;
    private int overlapSize;

    public SimpleDocumentSplitter(int chunkSize) {
        this.chunkSize = chunkSize;
        if (this.chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than 0, chunkSize: " + this.chunkSize);
        }
    }

    public SimpleDocumentSplitter(int chunkSize, int overlapSize) {
        this.chunkSize = chunkSize;
        this.overlapSize = overlapSize;

        if (this.chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than 0, chunkSize: " + this.chunkSize);
        }
        if (this.overlapSize >= this.chunkSize) {
            throw new IllegalArgumentException("overlapSize must be less than chunkSize, overlapSize: " + this.overlapSize + ", chunkSize: " + this.chunkSize);
        }
    }

    @Override
    public List<Document> split(Document document, DocumentIdGenerator idGenerator) {
        if (document == null || StringUtil.noText(document.getContent())) {
            return Collections.emptyList();
        }

        String content = document.getContent();
        int index = 0, currentIndex = index;
        int maxIndex = content.length();

        List<Document> chunks = new ArrayList<>();
        while (currentIndex < maxIndex) {
            int endIndex = Math.min(currentIndex + chunkSize, maxIndex);
            String chunk = content.substring(currentIndex, endIndex).trim();
            currentIndex = currentIndex + chunkSize - overlapSize;

            if (chunk.isEmpty()) {
                continue;
            }

            Document newDocument = Document.builder().build();
            newDocument.addMetadata(document.getMetadataMap());
            newDocument.addMetadata("index", currentIndex);
            newDocument.addMetadata("total", maxIndex);
            newDocument.setContent(chunk);
            newDocument.setId(idGenerator == null ? null : idGenerator.generateId(newDocument));
            chunks.add(newDocument);
        }

        return chunks;
    }
}
