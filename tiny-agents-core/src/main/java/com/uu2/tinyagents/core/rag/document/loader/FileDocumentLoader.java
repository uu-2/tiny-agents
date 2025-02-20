
package com.uu2.tinyagents.core.rag.document.loader;


import com.uu2.tinyagents.core.rag.document.DocumentParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class FileDocumentLoader extends AbstractDocumentLoader {

    private final File file;

    public FileDocumentLoader(DocumentParser documentParser, File file) {
        super(documentParser);
        this.file = file;
    }

    @Override
    protected Map<String, Object> getMetadata() {
        return Map.of("origin", file.getAbsoluteFile());
    }

    @Override
    protected InputStream loadInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
