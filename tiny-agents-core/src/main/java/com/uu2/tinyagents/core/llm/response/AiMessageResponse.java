package com.uu2.tinyagents.core.llm.response;

import com.uu2.tinyagents.core.message.AiMessage;
import com.uu2.tinyagents.core.message.tools.AiFunctionCall;
import com.uu2.tinyagents.core.prompt.Prompt;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AiMessageResponse extends AbstractMessageResponse<AiMessage> {
    private final AiMessage message;
    private final String response;
    private final Prompt prompt;

    public AiMessageResponse(Prompt prompt, String response, AiMessage message) {
        this.message = message;
        this.response = response;
        this.prompt = prompt;
    }

    @Override
    public AiMessage getMessage() {
        return message;
    }

    public List<FunctionCaller> getFunctionCallers() {
        if (this.message == null) {
            return Collections.emptyList();
        }

        List<AiFunctionCall> calls = message.getCalls();
        if (calls == null || calls.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Function> funcMap = prompt.getFunctionMap();

        if (funcMap == null || funcMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<FunctionCaller> functionCallers = new ArrayList<>(calls.size());
        for (AiFunctionCall call : calls) {
            Function function = funcMap.get(call.getName());
            if (function != null) {
                functionCallers.add(new FunctionCaller(function, call));
            }
        }
        return functionCallers;
    }

    public static AiMessageResponse error(Prompt prompt, String response, String errorMessage) {
        AiMessageResponse errorResp = new AiMessageResponse(prompt, response, null);
        errorResp.setError(true);
        errorResp.setErrorMessage(errorMessage);
        return errorResp;
    }


    @Override
    public String toString() {
        return "AiMessageResponse{" +
                "prompt=" + prompt +
                ", response='" + response + '\'' +
                ", message=" + message +
                ", error=" + error +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorType='" + errorType + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
