package com.uu2.tinyagents.core.rag.graph;

import lombok.Data;

@Data
public class ExecuteResult {
    private final String nodeId;
    private Status status;
    private Object result;
    private Exception exception;

    public ExecuteResult(String nodeId) {
        this.status = Status.RUNNING;
        this.nodeId = nodeId;
    }

}
