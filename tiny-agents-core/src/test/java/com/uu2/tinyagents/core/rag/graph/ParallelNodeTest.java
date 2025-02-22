package com.uu2.tinyagents.core.rag.graph;


import com.uu2.tinyagents.core.document.id.RandomIdGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ParallelNodeTest {

    @Test
    public void invoke() {
        Task t = new Task() {
            @Override
            public Object invoke(ExecuteContext ctx) {
                return "this task";
            }

            @Override
            public Object encode() {
                return null;
            }

            @Override
            public void decode(Object content) {

            }
        };

        String id = RandomIdGenerator.id();
        Node node = Node.builder()
                .id(id)
                .tasks(List.of(t, t, t, t))
                .build();

        ExecuteContext ctx = new ExecuteContext();
        try {
            node.invoke(ctx, null);

            System.out.println(ctx.getResults());

            Assert.assertEquals(1, ctx.getResults().size());
            Assert.assertTrue((ctx.getResults().get(id) instanceof List));
            Assert.assertEquals(4, ((List) ctx.getResults().get(id)).size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}