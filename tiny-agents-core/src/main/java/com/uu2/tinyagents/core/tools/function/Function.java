package com.uu2.tinyagents.core.tools.function;

import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
public abstract class Function implements Serializable {
    protected String name;
    protected String description;
    protected Parameter[] parameters;

    public abstract Object invoke(Map<String, Object> argsMap);
}
