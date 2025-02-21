package com.uu2.tinyagents.core.rag.store;

import com.uu2.tinyagents.core.message.Message;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class Query {

    private String text;
    private double[] vector;
    private List<Message> history;
    private Map<String, Object> filter;
    @Builder.Default
    private int topK = 6;
    @Builder.Default
    private double similarityThreshold = 0.0;
    @Builder.Default
    private boolean outputVector = false;

    public static Query of(String text) {
        return Query.builder().text(text).build();
    }

    public static Query of(double[] vector) {
        return Query.builder().vector(vector).build();
    }

    public String filterExpression() {
        return null;
    }
}
