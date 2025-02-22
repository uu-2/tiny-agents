package com.uu2.tinyagents.core.rag.graph.dsl;

import com.uu2.tinyagents.core.rag.graph.Edge;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class EdgeDef implements Serializable {
    private String type;
    private Object content;

    public EdgeDef(Edge edge) {
        this.type = edge.getClass().getName();
        this.content = edge.encode();
    }
}
