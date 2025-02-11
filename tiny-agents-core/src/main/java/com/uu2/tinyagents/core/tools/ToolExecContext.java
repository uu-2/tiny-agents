package com.uu2.tinyagents.core.tools;

import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.message.tools.ToolExecMessage;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ToolExecContext {
    private Map<String, Function> funcMap;
    private Map<String, Object> toolExecResults = new HashMap<>();

    protected ToolExecContext(Map<String, Function> funcMap) {
        this.funcMap = funcMap;
    }

    public List<Message> execCalls(List<AiFunctionCall> calls) {
        Queue<AiFunctionCall> callsQueue = new LinkedList<>(calls.stream().map(AiFunctionCall::new).toList());
        List<Message> execMessages = new LinkedList<>();

        AiFunctionCall stopFlag = null;
        while (!callsQueue.isEmpty()) {
            AiFunctionCall fc = callsQueue.poll();
            String dependenceTool = this.functionArgsCompute(fc);
            if (StringUtil.hasText(dependenceTool)) {
                if(stopFlag != fc) {
                    callsQueue.offer(fc);
                    stopFlag = fc;
                    continue;
                } else {
                    execMessages.add(HumanMessage.of("需要 ["+dependenceTool+"] 的调用信息和参数信息"));
                    break;
                }
            }

            Function f = this.funcMap.get(fc.getName());
            if (f == null) {
                continue;
            }
            Object execRes = f.invoke(fc.getArgs());
            toolExecResults.put(fc.getName(), execRes);
            execMessages.add(new ToolExecMessage(fc.getCallId(), execRes));
        }
        return execMessages;
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

    public static ToolExecContext of(Map<String, Function> funcMap) {
        return new ToolExecContext(funcMap);
    }
}
