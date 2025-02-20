
package com.uu2.tinyagents.core.rag.document.parser;


import com.uu2.tinyagents.core.rag.document.Document;
import com.uu2.tinyagents.core.rag.document.DocumentParser;
import com.uu2.tinyagents.core.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;

public class DefaultTextDocumentParser implements DocumentParser {
    @Override
    public Document parse(InputStream stream) {
        try {
            return Document.builder()
                    .content(IOUtil.readUtf8(stream))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
