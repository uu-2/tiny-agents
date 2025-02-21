package com.uu2.tinyagents.core.rag.graph;

import com.alibaba.fastjson.JSONObject;
import com.uu2.tinyagents.core.rag.graph.dsl.NodeDef;
import com.uu2.tinyagents.core.rag.graph.dsl.TaskDef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.uu2.tinyagents.core.rag.graph.dsl.PipeLineSerializer.deserialize;
import static com.uu2.tinyagents.core.rag.graph.dsl.PipeLineSerializer.serialize;

@Data
@SuperBuilder
@NoArgsConstructor
public class SwitchNode extends Node {
    private Task task;
    private Map<String, Node> nodes;

    public SwitchNode(Task task, Map<String, Node> nodes) {
        this.task = task;
        this.nodes = nodes;
    }

    @Override
    public void invoke(ExecuteContext ctx) throws InterruptedException {
        Object v = task.invoke(ctx);
        ctx.addResults(this.getId(), v);
        if (nodes.containsKey(String.valueOf(v))) {
            nodes.get(String.valueOf(v)).invoke(ctx);
        } else if (this.getNext() != null) {
            this.getNext().invoke(ctx);
        }
    }

    @Override
    public Object encode() {
        Map<String, NodeDef> nodeDefs = new HashMap<>();
        nodes.forEach((k, v) -> {
            nodeDefs.put(k, new NodeDef(v));
        });
        return Map.of(
                "task", new TaskDef(task),
                "nodes", nodeDefs
        );
    }

    @Override
    public void decode(Object content) {
        JSONObject c = (JSONObject) content;
        try {
            // 解析task
            TaskDef taskDef = c.getObject("task", TaskDef.class);
            this.task = (Task) Class.forName(taskDef.getType()).getDeclaredConstructor().newInstance();

            // 解析nodes
            JSONObject nodeDefs = c.getObject("nodes", JSONObject.class);

            nodeDefs.keySet().forEach(key -> {
                try {
                    NodeDef def = nodeDefs.getObject(key, NodeDef.class);
                    this.add(key, deserialize(def));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("SwitchNode decode",e);
        }
    }

    public void add(String key, Node node) {
        if (this.nodes == null) {
            this.nodes = new HashMap<>();
        }
        this.nodes.put(key, node);
    }
}
