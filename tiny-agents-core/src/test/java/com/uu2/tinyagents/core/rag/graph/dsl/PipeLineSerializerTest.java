package com.uu2.tinyagents.core.rag.graph.dsl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.uu2.tinyagents.core.rag.graph.*;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class PipeLineSerializerTest {

    @Test
    public void serialize() throws InterruptedException {

        StateGraph stateGraph = new StateGraph();
        stateGraph.add(StateGraph.START, new Node("node1 - task", new TestTask("node1 - task")));
        stateGraph.add(new Node("node2 - task", new TestTask("node2 - task")));
        stateGraph.add("node1 - task", "node2 - task");

        stateGraph.add(new Node("node3 - task", new TestTask("node3 - task")));
        stateGraph.add(new Node("node4 - task", new TestTask("node4 - task")));
        stateGraph.add(new Node("node5 - task", new TestTask("node5 - task")));
        stateGraph.add(new Node("node6 - task", new TestTask("node6 - task")));

        stateGraph.add("node2 - task", Condition.Any(RandomUtil.randomEle(
                List.of("node3 - task", "node4 - task", "node5 - task")
        )), Map.of(
                "node3 - task", "node3 - task",
                "node4 - task", "node4 - task",
                "node5 - task", "node5 - task"
        ));
        stateGraph.add("node4 - task", "node6 - task");
        stateGraph.add("node3 - task", new Node("node7 - task", new TestTask("node7 - task")));

        String json = PipeLineSerializer.serialize(stateGraph);
        StateGraph pl2 = PipeLineSerializer.deserialize(json);

        ExecuteContext ctx = new ExecuteContext();
        pl2.invoke(ctx);

        System.out.println(JSON.toJSONString(ctx.getResults(), SerializerFeature.PrettyFormat));
    }
}