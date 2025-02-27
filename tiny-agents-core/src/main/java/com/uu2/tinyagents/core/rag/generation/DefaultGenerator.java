package com.uu2.tinyagents.core.rag.generation;

import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.message.HumanMessage;
import com.uu2.tinyagents.core.prompt.FunctionPrompt;
import com.uu2.tinyagents.core.prompt.HistoriesPrompt;
import com.uu2.tinyagents.core.rag.Question;
import com.uu2.tinyagents.core.rag.graph.ExecuteContext;
import com.uu2.tinyagents.core.rag.graph.Task;
import com.uu2.tinyagents.core.tools.ToolExecContext;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class DefaultGenerator implements Task {
    private FunctionPrompt prompt;
    private Llm llm;

    public DefaultGenerator(Llm llm, FunctionPrompt prompt) {
        this.prompt = prompt;
        this.llm = llm;
    }

    @Override
    public Object invoke(ExecuteContext ctx) {
        Question question = ctx.getQuestion();
        if (StringUtil.hasText(question.getPromptTemplate())) {
            prompt.addMessage(HumanMessage.of(question.getPromptTemplate()));
        } else {
            prompt.addMessage(HumanMessage.of(question.getText()));
        }

        Map<String, Object> params = new HashMap<>(Map.of(
                "question", question.getText()
        ));

        if (question.getDocuments() != null) {
            params.put("documents", question.getDocuments());
        }

        if (ctx.getParams() != null) {
            params.putAll(ctx.getParams());
        }
        params.put("state", ctx.getResults());
        prompt.setParams(params);
        return ToolExecContext.of(prompt).execChat(llm);
    }

}
