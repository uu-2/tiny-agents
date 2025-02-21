package com.uu2.tinyagents.core.rag.graph;

public interface Task {

    Object invoke(ExecuteContext ctx);

    Object encode();

    void decode(Object content);
}
