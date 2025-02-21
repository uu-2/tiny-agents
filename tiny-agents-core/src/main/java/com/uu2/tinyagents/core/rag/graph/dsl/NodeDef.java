package com.uu2.tinyagents.core.rag.graph.dsl;

import com.uu2.tinyagents.core.rag.graph.Node;
import lombok.Data;

import java.io.Serializable;

@Data
public class NodeDef implements Serializable {
    private String id;
    private String type;
    private NodeDef next;
    private Object content;

    public NodeDef() {
    }
    public NodeDef(Node node) {
        this.id = node.getId();
        this.type = node.getClass().getTypeName();
        if (node.getNext() != null) {
            this.next = new NodeDef(node.getNext());
        }
        this.content = node.encode();
    }
}
