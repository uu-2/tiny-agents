package com.uu2.tinyagents.core.rag.graph;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public class ExecuteContext {
    private final Map<String, Object> results;
    private final Map<String, Object> params;

    public ExecuteContext(Map<String, Object> params) {
        this.params = params;
        this.results = new HashMap<>();
    }

    public ExecuteContext() {
        this.params = Collections.EMPTY_MAP;
        this.results = new HashMap<>();
    }

    public void addResults(String id, Object results) {
        this.results.put(id, results);
    }
}
