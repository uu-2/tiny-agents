
package com.uu2.tinyagents.core.document.loader;


import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.DocumentLoader;
import com.uu2.tinyagents.core.document.DocumentParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class AbstractDocumentLoader implements DocumentLoader {

    protected DocumentParser documentParser;

    public AbstractDocumentLoader(DocumentParser documentParser) {
        this.documentParser = documentParser;
    }

    @Override
    public Document load() {
        try (InputStream stream = loadInputStream()){
            Document document = documentParser.parse(stream);
            document.setMetadataMap(getMetadata());
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Map<String, Object> getMetadata();

    protected abstract InputStream loadInputStream();

}
