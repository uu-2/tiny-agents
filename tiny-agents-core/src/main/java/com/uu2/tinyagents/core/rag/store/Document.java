package com.uu2.tinyagents.core.rag.store;

public interface Document {
    String content();

    String id();

    String origin();
}
