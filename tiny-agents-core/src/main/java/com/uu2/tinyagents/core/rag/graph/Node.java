package com.uu2.tinyagents.core.rag.graph;

import com.uu2.tinyagents.core.rag.document.id.RandomIdGenerator;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@SuperBuilder
public abstract class Node {
    public static final Node DEFAULT = new Node() {
        @Override
        public void invoke(ExecuteContext ctx) throws InterruptedException {

        }
    };
    private String id;

    private Node next;

    public Node() {
        this.id = RandomIdGenerator.id();
    }

    public Node(String id) {
        this.id = id;
    }

    public Node(String id, Node next) {
        this.id = id;
        this.next = next;
    }


    public abstract void invoke(ExecuteContext ctx) throws InterruptedException;

    public Object encode() {
        return null;
    }

    public void decode(Object content) {
    }

}
