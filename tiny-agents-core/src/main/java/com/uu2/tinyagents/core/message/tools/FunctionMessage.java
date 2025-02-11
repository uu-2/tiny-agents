package com.uu2.tinyagents.core.message.tools;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class FunctionMessage implements Serializable {
    private final String type;
    private final FunctionInfo function;

    @Getter
    public static class FunctionInfo {
        protected String name;
        protected String description;
        protected Parameters parameters;
    }

    @Getter
    public static class Parameters {
        protected String type;
        protected Map<String, Object> properties;
        protected Set<String> required;
    }

    public FunctionMessage(String name, String description) {
        this.type = "function";
        this.function = new FunctionInfo();
        this.function.name = name;
        this.function.description = description;
    }

    public void addParameter(String name, String type, String desc, boolean required, String... enums) {
        if (this.function.parameters == null) {
            this.function.parameters = new Parameters();
            this.function.parameters.type = "object";
            this.function.parameters.properties = new HashMap<>();
            this.function.parameters.required = new HashSet<>();
        }

        Map<String, Object> p = null;
        if (enums != null) {
            p = Map.of("type", type, "description", desc, "enum", enums);
        } else {
            p = Map.of("type", type, "description", desc);
        }
        this.function.parameters.properties.put(name, p);
        if (required) {
            this.function.parameters.required.add(name);
        }
    }

}
