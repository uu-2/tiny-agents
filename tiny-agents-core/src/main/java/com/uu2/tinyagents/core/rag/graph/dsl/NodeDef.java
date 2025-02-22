package com.uu2.tinyagents.core.rag.graph.dsl;

import com.uu2.tinyagents.core.rag.graph.Node;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class NodeDef implements Serializable {
    private String id;
    private String type;
    private Object content;

    public NodeDef(Node node) {
        this.id = node.getId();
        this.type = node.getClass().getTypeName();
        this.content = node.encode();
    }
}
