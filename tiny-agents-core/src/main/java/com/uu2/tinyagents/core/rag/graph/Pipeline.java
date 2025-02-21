package com.uu2.tinyagents.core.rag.graph;

import java.util.HashMap;
import java.util.Map;

public class Pipeline {

    private final Node sentry;
    private Node tail;

    private Map<String, Node> nodes;

    public Pipeline(Node head) {
        this();
        this.sentry.setNext(head);
        this.tail = head;
    }

    public Pipeline() {
        this.sentry = Node.DEFAULT;
        this.nodes = new HashMap<>();
    }

    public boolean add(Node node) {
        if (this.nodes.containsKey(node.getId())) {
            return false;
        }

        if (this.tail == null) {
            this.tail = node;
            this.sentry.setNext(node);
        } else {
            this.tail.setNext(node);
            this.tail = node;
        }
        return true;
    }

    public void invoke(ExecuteContext ctx) throws InterruptedException {
        this.sentry.getNext().invoke(ctx);
    }

    public Node getHead() {
        return this.sentry.getNext();
    }
}
