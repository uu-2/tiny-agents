package com.uu2.tinyagents.core.rag.document;

import java.io.InputStream;

public interface DocumentParser {
    Document parse(InputStream stream);
}
