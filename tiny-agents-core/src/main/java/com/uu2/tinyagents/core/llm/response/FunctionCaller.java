package com.uu2.tinyagents.core.llm.response;

import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.tools.function.Function;
import lombok.Data;

@Data
public class FunctionCaller {
    private final Function function;
    private final AiFunctionCall aiFunctionCall;

    public Object call() {
        return function.invoke(this.aiFunctionCall.getArgs());
    }
}
