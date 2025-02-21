package com.uu2.tinyagents.core.rag.graph.dsl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.uu2.tinyagents.core.rag.graph.*;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class PipeLineSerializerTest {

    public static class Task1 implements Task {
        @Override
        public Object invoke(ExecuteContext ctx) {
            return "Task1";
        }

        @Override
        public Object encode() {
            return "hahah";
        }

        @Override
        public void decode(Object content) {

        }
    }

    @Test
    public void serialize() throws InterruptedException {

        Pipeline pipeline = new Pipeline();
        pipeline.add(new SingleNode(new Task1()));
        pipeline.add(new SingleNode(new Task1()));
        pipeline.add(new SingleNode(new Task1()));
        pipeline.add(new ParallelNode(
                new Task1(),
                new Task1(),
                new Task1()
        ));
        pipeline.add(new SwitchNode(
                new Task1(),
                Map.of(
                        "SwitchNod5 - 1", new SingleNode(new Task1()),
                        "SwitchNod5 - 2", new SingleNode(new Task1()),
                        "SwitchNod5 - 3", new SingleNode(new Task1())
                )
        ));

        String json = PipeLineSerializer.serialize(pipeline);
        Pipeline pl2 = PipeLineSerializer.deserialize(json);

        ExecuteContext ctx = new ExecuteContext();
        pl2.invoke(ctx);

        System.out.println(JSON.toJSONString(ctx.getResults(), SerializerFeature.PrettyFormat));
    }
}