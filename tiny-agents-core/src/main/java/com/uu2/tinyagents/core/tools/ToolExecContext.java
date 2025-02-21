package com.uu2.tinyagents.core.tools;

import com.alibaba.fastjson.JSON;
import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.message.tools.ToolExecMessage;
import com.uu2.tinyagents.core.prompt.FunctionPrompt;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ToolExecContext {
    private final FunctionPrompt prompt;
    private final Map<String, Function> funcMap;
    private final Map<String, Object> toolExecResults = new HashMap<>();

    protected ToolExecContext(FunctionPrompt prompt) {
        this.prompt = prompt;

        this.funcMap = prompt.getFunctionMap();
    }

    public AiMessageResponse execChat(final Llm llm) {
        AiMessageResponse response = llm.chat(prompt);
        prompt.processAssistantMessage(response.getMessage());

        while (prompt.isHasToolCalls()) {
            this.doCalls(response.getMessage());

            response = llm.chat(prompt);
            prompt.processAssistantMessage(response.getMessage());
        }

        return response;
    }

    private void doCalls(AiMessage message) {

        List<AiFunctionCall> calls = message.getCalls();
        Queue<AiFunctionCall> callsQueue = new LinkedList<>(calls.stream().map(AiFunctionCall::new).toList());
        List<Message> execMessages = new LinkedList<>();

        AiFunctionCall stopFlag = null;
        while (!callsQueue.isEmpty()) {
            AiFunctionCall fc = callsQueue.poll();
            String dependenceTool = this.functionArgsCompute(fc);
            if (StringUtil.hasText(dependenceTool)) {
                if (stopFlag != fc) {
                    callsQueue.offer(fc);
                    stopFlag = fc;
                    continue;
                } else {
                    execMessages.add(HumanMessage.of("需要 [" + dependenceTool + "] 的调用信息和参数信息"));
                    break;
                }
            }

            Function f = this.funcMap.get(fc.getName());
            if (f == null) {
                continue;
            }
            Object execRes = f.invoke(fc.getArgs());
            System.out.println(">>> tool execute [" + fc.getName() + "]: " + JSON.toJSONString(message) + " ===> " + JSON.toJSONString(execRes));
            toolExecResults.put(fc.getName(), execRes);
            execMessages.add(new ToolExecMessage(fc.getCallId(), execRes));
        }
        prompt.addMessage(execMessages);
    }

    private String functionArgsCompute(AiFunctionCall fc) {
        AtomicReference<String> dependenceTool = new AtomicReference<>();
        fc.getArgs().forEach((k, v) -> {
            if (String.valueOf(v).contains("$ref")) {
                // 获取 $ref(name) 中的name, 使用正则表达式
                String regex = "\\$ref\\(([^）]*?)\\)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(String.valueOf(v));
                if (matcher.find()) {
                    String refName = matcher.group(1);
                    if (toolExecResults.containsKey(refName)) {
                        fc.getArgs().put(k, toolExecResults.get(refName));
                    } else {
                        dependenceTool.set(refName);
                    }
                }
            }
        });
        return dependenceTool.get();
    }

    public static ToolExecContext of(FunctionPrompt prompt) {
        return new ToolExecContext(prompt);
    }
}
