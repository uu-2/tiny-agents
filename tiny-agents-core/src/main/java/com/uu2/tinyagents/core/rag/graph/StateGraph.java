package com.uu2.tinyagents.core.rag.graph;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class StateGraph {

    public static final String START = "start";

    private Map<String, Node> nodes;
    private Map<String, Edge> edges;

    public StateGraph() {
        this.nodes = new HashMap<>();
    }

    public boolean add(Node node) {
        if (this.nodes.containsKey(node.getId())) {
            return false;
        }
        this.nodes.put(node.getId(), node);
        return true;
    }

    public boolean add(String from, String to) {
        if ((START.equals(from) || this.nodes.containsKey(from)) && this.nodes.containsKey(to)) {
            return this.add(from, Condition.Any(to), Map.of(to, to));
        } else {
            return false;
        }
    }

    public boolean add(String from, Node to) {
        this.nodes.put(to.getId(), to);
        return this.add(from, to.getId());
    }

    public boolean add(Node from, Node to) {
        this.nodes.put(from.getId(), from);
        this.nodes.put(to.getId(), to);
        return this.add(from.getId(), to.getId());
    }

    public boolean add(String from, Condition cond, Map<String, Object> toNodes) {
        if (!START.equals(from) && !this.nodes.containsKey(from)) {
            return false;
        }
        Map<String, String> toMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : toNodes.entrySet()) {
            if (entry.getValue() instanceof Node node) {
                this.nodes.put(node.getId(), node);
                toMap.put(entry.getKey(), node.getId());
            } else {
                String toNodeId = entry.getValue().toString();
                if (!this.nodes.containsKey(toNodeId)) {
                    return false;
                }
                toMap.put(entry.getKey(), toNodeId);
            }
        }

        this.add(new Edge(from, cond, toMap));
        return true;
    }

    public Object invoke(ExecuteContext ctx) throws InterruptedException {
        Edge edge = this.edges.get(START);
        String prevNodeId = START;
        while (edge != null) {
            String nodeId = edge.invoke(ctx);
            Node node = this.nodes.get(nodeId);
            node.invoke(ctx, prevNodeId);

            prevNodeId = nodeId;
            // next edge
            edge = this.edges.get(nodeId);
        }
        return ctx.getResults().getOrDefault(prevNodeId, null);
    }

    public void add(Edge edge) {
        if (this.edges == null) {
            this.edges = new HashMap<>();
        }
        this.edges.put(edge.getFrom(), edge);
    }
}
