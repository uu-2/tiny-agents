package com.uu2.tinyagents.core.rag.graph;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TestTask implements Task {
    private String name;

    public TestTask(String name) {
        this.name = name;
    }

    @Override
    public Object invoke(ExecuteContext ctx) {
        return name;
    }

    @Override
    public Object encode() {
        return name;
    }

    @Override
    public void decode(Object content) {
        this.name = (String) content;
    }
}
