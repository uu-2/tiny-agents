package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.tools.function.JavaNativeFunctions;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public abstract class Prompt {
    protected List<Function> functions;

    public List<Message> messages() {
        return this.messages(null);
    }
    public abstract List<Message> messages(Map<String, Object> params);

    public Map<String, Function> getFunctionMap() {
        return functions.stream().collect(Collectors.toMap(Function::getName, f -> f));
    }

    public void addFunctions(Collection<Function> functions) {
        if (this.functions == null)
            this.functions = new java.util.ArrayList<>();
        this.functions.addAll(functions);
    }

    public void addFunctions(Class<?> funcClass, String... methodNames) {
        if (this.functions == null)
            this.functions = new java.util.ArrayList<>();
        this.functions.addAll(JavaNativeFunctions.from(funcClass, methodNames));
    }

    public void addFunctions(Object funcObject, String... methodNames) {
        if (this.functions == null)
            this.functions = new java.util.ArrayList<>();
        this.functions.addAll(JavaNativeFunctions.from(funcObject, methodNames));
    }

    public void processAssistantMessage(AiMessage lastAiMessage) {
        lastAiMessage.setRole(Message.Role.ASSISTANT.getRole());
    }
}
