package com.uu2.tinyagents.core.rag.graph;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uu2.tinyagents.core.rag.graph.dsl.TaskDef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Data
@SuperBuilder
@NoArgsConstructor
public class ParallelNode extends Node {
    private List<Task> tasks;

    public ParallelNode(List<Task> tasks) {
        this.tasks = tasks;
    }

    public ParallelNode(Task... tasks) {
        this.tasks = List.of(tasks);
    }

    @Override
    public void invoke(ExecuteContext ctx) throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        List<Callable<Object>> forkJoinTasks = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            Callable<Object> forkJoinTask = () -> task.invoke(ctx);
            forkJoinTasks.add(forkJoinTask);
        }

        // 提交所有任务并等待完成
        List<Future<Object>> futures = forkJoinPool.invokeAll(forkJoinTasks);
        List<Object> results = new ArrayList<>(futures.size());
        for (Future<Object> future : futures) {
            try {
                results.add(future.get());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        ctx.addResults(this.getId(), results);

        // 所有任务完成后，继续执行下一个节点
        if (this.getNext() != null) {
            this.getNext().invoke(ctx);
        }

    }

    @Override
    public Object encode() {
        return tasks.stream().map(TaskDef::new).toList();
    }

    @Override
    public void decode(Object content) {
        JSONArray contentArray = (JSONArray) content;

        for (int i = 0; i < contentArray.size(); i++) {
            TaskDef def = contentArray.getObject(i, TaskDef.class);
            try {
                Task task = (Task) Class.forName(def.getType()).getDeclaredConstructor().newInstance();
                task.decode(def.getContent());
                this.add(task);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void add(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
    }
}
