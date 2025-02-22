
package com.uu2.tinyagents.core.document.splitter;


import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.DocumentSplitter;
import com.uu2.tinyagents.core.document.id.DocumentIdGenerator;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleTokenizeSplitter implements DocumentSplitter {
    private EncodingRegistry registry = Encodings.newLazyEncodingRegistry();
    private EncodingType encodingType = EncodingType.CL100K_BASE;
    private int chunkSize;
    private int overlapSize;

    public SimpleTokenizeSplitter(int chunkSize) {
        this.chunkSize = chunkSize;
        if (this.chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than 0, chunkSize: " + this.chunkSize);
        }
    }

    public SimpleTokenizeSplitter(int chunkSize, int overlapSize) {
        this.chunkSize = chunkSize;
        this.overlapSize = overlapSize;

        if (this.chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than 0, chunkSize: " + this.chunkSize);
        }
        if (this.overlapSize >= this.chunkSize) {
            throw new IllegalArgumentException("overlapSize must be less than chunkSize, overlapSize: " + this.overlapSize + ", chunkSize: " + this.chunkSize);
        }
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getOverlapSize() {
        return overlapSize;
    }

    public void setOverlapSize(int overlapSize) {
        this.overlapSize = overlapSize;
    }

    public EncodingRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(EncodingRegistry registry) {
        this.registry = registry;
    }

    public EncodingType getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    @Override
    public List<Document> split(Document document, DocumentIdGenerator idGenerator) {
        if (document == null || StringUtil.noText(document.getContent())) {
            return Collections.emptyList();
        }

        String content = document.getContent();
        Encoding encoding = this.registry.getEncoding(this.encodingType);

        List<Integer> tokens = encoding.encode(content).boxed();


        int index = 0, currentIndex = index;
        int maxIndex = tokens.size();

        List<Document> chunks = new ArrayList<>();
        while (currentIndex < maxIndex) {
            int endIndex = Math.min(currentIndex + chunkSize, maxIndex);
            List<Integer> chunkTokens = tokens.subList(currentIndex, endIndex);

            IntArrayList intArrayList = new IntArrayList();
            for (Integer chunkToken : chunkTokens) {
                intArrayList.add(chunkToken);
            }
            String chunkText = encoding.decode(intArrayList).trim();
            if (chunkText.isEmpty()) {
                continue;
            }

            //UTF-8 'Unicode replacement character' which in your case is 0xFFFD (65533 in Hex).
            //fix 修复中文乱码的问题
            boolean firstIsReplacement = chunkText.charAt(0) == 65533;
            boolean lastIsReplacement = chunkText.charAt(chunkText.length() - 1) == 65533;

            if (firstIsReplacement || lastIsReplacement) {
                if (firstIsReplacement) currentIndex -= 1;
                if (lastIsReplacement) endIndex += 1;

                chunkTokens = tokens.subList(currentIndex, endIndex);
                intArrayList = new IntArrayList();
                for (Integer chunkToken : chunkTokens) {
                    intArrayList.add(chunkToken);
                }

                chunkText = encoding.decode(intArrayList).trim();
            }

            currentIndex = currentIndex + chunkSize - overlapSize;

            Document newDocument = Document.builder().build();
            newDocument.addMetadata(document.getMetadataMap());
            newDocument.addMetadata("index", currentIndex);
            newDocument.addMetadata("total", maxIndex);
            newDocument.setContent(chunkText);

            //we should invoke setId after setContent
            newDocument.setId(idGenerator == null ? null : idGenerator.generateId(newDocument));
            chunks.add(newDocument);
        }

        return chunks;
    }
}
