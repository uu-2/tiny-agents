package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.tools.ToolCallsMessage;
import com.uu2.tinyagents.core.message.tools.ToolExecMessage;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.tools.function.Function;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class FunctionPrompt extends TextPrompt {
    protected final List<Message> fullMessage;
    private boolean hasTools = false;
    private Map<String, Object> toolExecResults = new HashMap<>();

    public FunctionPrompt(String content) {
        super(content);
        fullMessage = new LinkedList<>();
    }

    public FunctionPrompt(String content, Class<?>... functionsClass) {
        super(content);
        fullMessage = new LinkedList<>();
        for (Class<?> aClass : functionsClass) {
            this.addFunctions(aClass);
        }
    }

    public FunctionPrompt(String content, Function... functions) {
        super(content);
        fullMessage = new LinkedList<>();
        for (Function function : functions) {
            this.addFunctions(function);
        }
    }

    public FunctionPrompt(String content, List<Function> functions) {
        super(content);
        fullMessage = new LinkedList<>();
        this.addFunctions(functions);
    }

    public List<Message> messages() {
        if (fullMessage.isEmpty()) {
            fullMessage.addAll(super.messages());
        }
        return fullMessage;
    }

    public void processAssistantMessage(AiMessage lastAiMessage) {
        super.processAssistantMessage(lastAiMessage);
        fullMessage.add(new ToolCallsMessage(lastAiMessage.getCalls()));

        hasTools = false;
        toolExecResults = new HashMap<>();
        if (lastAiMessage.getCalls() != null && !lastAiMessage.getCalls().isEmpty()) {
            hasTools = true;
            Map<String, Function> funcMap = this.getFunctionMap();

            for (AiFunctionCall fc : lastAiMessage.getCalls()) {
                Function f = funcMap.get(fc.getName());
                if (f == null) {
                    continue;
                }
                Object execRes = f.invoke(fc.getArgs());
                toolExecResults.put(fc.getName(), execRes);
                fullMessage.add(new ToolExecMessage(fc.getCallId(), execRes));
            }
        }
    }
}
