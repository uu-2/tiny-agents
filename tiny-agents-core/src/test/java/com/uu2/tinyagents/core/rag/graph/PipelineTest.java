package com.uu2.tinyagents.core.rag.graph;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class PipelineTest {

    public static class Task1 implements Task {

        private String name;

        public Task1(String name) {
            this.name = name;
        }


        @Override
        public Object invoke(ExecuteContext ctx) {
            return name;
        }

        @Override
        public Object encode() {
            return null;
        }

        @Override
        public void decode(Object content) {

        }
    }


    @Test
    public void invoke() throws InterruptedException {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new SingleNode(new Task1("SingleNode1")));
        pipeline.add(new SingleNode(new Task1("SingleNode2")));
        pipeline.add(new SingleNode(new Task1("SingleNode3")));
        pipeline.add(new ParallelNode(
                new Task1("ParallelNode4 - t1"),
                new Task1("ParallelNode4 - t2"),
                new Task1("ParallelNode4 - t3")
        ));
        pipeline.add(new SwitchNode(
                new Task1(RandomUtil.randomEle(new String[]{"SwitchNod5 - 1", "SwitchNod5 - 2", "SwitchNod5 - 3"})),
                Map.of(
                        "SwitchNod5 - 1", new SingleNode(new Task1("SwitchNod5 - 1 -Node")),
                        "SwitchNod5 - 2", new SingleNode(new Task1("SwitchNod5 - 2 -Node")),
                        "SwitchNod5 - 3", new SingleNode(new Task1("SwitchNod5 - 3 -Node"))
                )
        ));

        ExecuteContext ctx = new ExecuteContext();
        pipeline.invoke(ctx);

        System.out.println(
                JSON.toJSONString(ctx.getResults(),
                        SerializerFeature.PrettyFormat,
                        SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteDateUseDateFormat)
        );
    }
}