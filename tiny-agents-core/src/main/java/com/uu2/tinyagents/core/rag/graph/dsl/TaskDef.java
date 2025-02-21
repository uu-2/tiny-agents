package com.uu2.tinyagents.core.rag.graph.dsl;

import com.uu2.tinyagents.core.rag.graph.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TaskDef implements Serializable {
    private String type;
    private Object content;

    public TaskDef(Task task) {
        this.type = task.getClass().getName();
        this.content = task.encode();
    }

}
