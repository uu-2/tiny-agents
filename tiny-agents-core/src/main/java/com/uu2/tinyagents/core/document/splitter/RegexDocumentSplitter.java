
package com.uu2.tinyagents.core.document.splitter;


import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.DocumentSplitter;
import com.uu2.tinyagents.core.document.id.DocumentIdGenerator;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegexDocumentSplitter implements DocumentSplitter {

    private final String regex;

    public RegexDocumentSplitter(String regex) {
        this.regex = regex;
    }

    @Override
    public List<Document> split(Document document, DocumentIdGenerator idGenerator) {
        if (document == null || StringUtil.noText(document.getContent())) {
            return Collections.emptyList();
        }
        String[] textArray = document.getContent().split(regex);
        List<Document> chunks = new ArrayList<>(textArray.length);
        for (int i = 0; i < textArray.length; i++) {
            String textString = textArray[i];
            Document newDocument = Document.builder().build();
            newDocument.addMetadata(document.getMetadataMap());
            newDocument.addMetadata("index", i);
            newDocument.addMetadata("total", textArray.length);
            newDocument.setContent(textString);

            //we should invoke setId after setContent
            newDocument.setId(idGenerator == null ? null : idGenerator.generateId(newDocument));
            chunks.add(newDocument);
        }
        return chunks;
    }
}
