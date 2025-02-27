package com.uu2.tinyagents.core.rag.graph;

public interface Task {

    Object invoke(ExecuteContext ctx);

    default Object encode() {
        return null;
    }

    default void decode(Object content) {
    }
}
