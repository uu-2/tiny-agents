
package com.uu2.tinyagents.core.prompt;


import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.tools.FunctionMessage;
import com.uu2.tinyagents.core.tools.function.Function;
import com.uu2.tinyagents.core.tools.function.Parameter;

import java.util.ArrayList;
import java.util.List;

public class DefaultPromptFormat implements PromptFormat {

    @Override
    public Object messagesFormat(Prompt prompt) {
        List<Message> messages = prompt.messages();
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        return messages.stream().map(Message::prompt).toList();
    }


    @Override
    public Object toolsFormat(Prompt prompt) {

        List<Function> functions = prompt.getFunctions();
        if (functions == null || functions.isEmpty()) {
            return null;
        }

        return buildFunctionJsonArray(functions);

    }

    protected List<Object> buildFunctionJsonArray(List<Function> functions) {
        List<Object> functionsJsonArray = new ArrayList<>();
        for (Function function : functions) {
            FunctionMessage functionMessage = new FunctionMessage(function.getName(), function.getDescription());


            for (Parameter parameter : function.getParameters()) {
                functionMessage.addParameter(
                        parameter.getName(),
                        parameter.getType(),
                        parameter.getDescription(),
                        parameter.isRequired(),
                        parameter.getEnums()
                );
            }

            functionsJsonArray.add(functionMessage);
        }

        return functionsJsonArray;
    }

}
