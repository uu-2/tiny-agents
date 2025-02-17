package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.memory.ChatMemory;
import com.uu2.tinyagents.core.memory.DefaultChatMemory;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.SystemMessage;
import com.uu2.tinyagents.core.tools.function.Function;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FunctionPrompt extends AttachmentPrompt {
    private boolean hasToolCalls = false;
    private Map<String, Object> toolExecResults = new HashMap<>();

    public FunctionPrompt(String content) {
        this(content, new DefaultChatMemory());
    }

    public FunctionPrompt(String content, ChatMemory memory) {
        super(memory);
        this.addMessage(HumanMessage.of(content));
    }

    @Override
    public SystemMessage getSystemMessage() {
        SystemMessage msg = super.getSystemMessage();
        if (msg == null) {
            return SystemMessage.of(
                    "你是一个工具调用助手, 你能够使用工具解决问题。并能根据工具执行结果生成问题答案。\n" +
                            "你应该优先使用工具解决问题, 并且仅仅使用工具解决问题, 你不应该使用工具以外的方法解决问题.\n" +
                            "当需要使用多个工具时，需要返回每个工具得详细调用信息。如果一个参数需要使用到其他工具得返回结果时，请在参数值中使用 $ref(tool) 格式。\n"
            );
        }
        return SystemMessage.of(String.valueOf(msg.getContent()) + "\n" +
                "你应该优先使用工具解决问题, 并且仅仅使用工具解决问题, 你不应该使用工具以外的方法解决问题.\n" +
                "当需要使用多个工具时，需要返回每个工具得详细调用信息。如果一个参数需要使用到其他工具得返回结果时，请在参数值中使用 $ref(tool) 格式。\n");
    }

    public FunctionPrompt(String content, Class<?>... functionsClass) {
        this(content);
        for (Class<?> aClass : functionsClass) {
            this.addFunctions(aClass);
        }
    }

    public FunctionPrompt(String content, Function... functions) {
        this(content);
        for (Function function : functions) {
            this.addFunctions(function);
        }
    }

    public FunctionPrompt(String content, List<Function> functions) {
        this(content);
        this.addFunctions(functions);
    }

    public void processAssistantMessage(AiMessage lastAiMessage) {
        super.processAssistantMessage(lastAiMessage);

        hasToolCalls = false;
        toolExecResults = new HashMap<>();
        hasToolCalls = (lastAiMessage.getCalls() != null && !lastAiMessage.getCalls().isEmpty());
    }
}
