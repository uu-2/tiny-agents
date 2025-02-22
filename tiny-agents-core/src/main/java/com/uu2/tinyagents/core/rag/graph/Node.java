package com.uu2.tinyagents.core.rag.graph;

import com.alibaba.fastjson.JSONArray;
import com.uu2.tinyagents.core.document.id.RandomIdGenerator;
import com.uu2.tinyagents.core.rag.graph.dsl.TaskDef;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Slf4j
@Data
@SuperBuilder
public class Node {
    private String id;
    private List<Task> tasks;


    public Node(List<Task> tasks) {
        this();
        this.tasks = tasks;
    }

    public Node(Task... tasks) {
        this();
        this.tasks = List.of(tasks);
    }

    public Node(String id, Task... tasks) {
        this(id);
        this.tasks = List.of(tasks);
    }

    public Node() {
        this.id = RandomIdGenerator.id();
    }

    public Node(String id) {
        this.id = id;
    }

    public void invoke(ExecuteContext ctx, String prevNodeId) throws InterruptedException {
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
    }

    public Object encode() {
        return tasks.stream().map(TaskDef::new).toList();
    }

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
