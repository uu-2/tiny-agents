package com.uu2.tinyagents.core.rag.graph;

public enum Status {
    READY(0), // 未开始执行
    RUNNING(1), // 已开始执行，执行中...
    ERROR(10), //发生错误
    FINISHED_NORMAL(20), //正常结束
    FINISHED_ABORT(21), //错误结束
    ;

    final int value;

    Status(int value) {
        this.value = value;
    }
}
