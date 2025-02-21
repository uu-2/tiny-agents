package com.uu2.tinyagents.core.rag.graph;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.uu2.tinyagents.core.rag.graph.dsl.TaskDef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
public class SingleNode extends Node {
    private Task task;

    public SingleNode(Task task) {
        this.task = task;
    }

    @Override
    public void invoke(ExecuteContext ctx) throws InterruptedException {
        Object v = task.invoke(ctx);
        ctx.addResults(this.getId(), v);

        if (this.getNext() != null) {
            this.getNext().invoke(ctx);
        }
    }

    @Override
    public Object encode() {
        return new TaskDef(task);
    }

    @Override
    public void decode(Object content) {
        JSONObject c = (JSONObject) content;
        String type = c.getString("type");
        try {
            this.task = (Task) Class.forName(type).getDeclaredConstructor().newInstance();
            this.task.decode(c.get("content"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
