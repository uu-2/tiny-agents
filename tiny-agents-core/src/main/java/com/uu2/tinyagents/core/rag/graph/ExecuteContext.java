package com.uu2.tinyagents.core.rag.graph;

import com.uu2.tinyagents.core.rag.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public class ExecuteContext {
    private final Map<String, ExecuteResult> results;
    private final Map<String, Object> params;
    @Setter
    private Question question;
    @Setter
    private ExecuteResult currentResult;

    public ExecuteContext(Question question, Map<String, Object> params) {
        this.params = params;
        this.results = new HashMap<>();
        this.question = question;
    }

    public ExecuteContext(Question question) {
        this.params = Collections.EMPTY_MAP;
        this.results = new HashMap<>();
        this.question = question;
    }

    public ExecuteContext() {
        this.params = Collections.EMPTY_MAP;
        this.results = new HashMap<>();
        this.question = null;
    }

    public void addResults(ExecuteResult result) {
        this.results.put(result.getNodeId(), result);
    }

}
