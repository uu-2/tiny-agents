package com.uu2.tinyagents.core.rag.preretrieval;

import com.uu2.tinyagents.core.llm.Llm;
import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import com.uu2.tinyagents.core.prompt.TextPrompt;
import com.uu2.tinyagents.core.document.Document;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryTranslation implements PreRetrieval {

    private Llm llm;

    public QueryTranslation(Llm llm) {
        this.llm = llm;
    }

    @Override
    public List<Document> invoke(List<Document> documents) {
        TextPrompt prompt = TextPrompt.of("""
                你是一个AI语言模型助手。你的任务是生成给定用户问题的五个不同版本，以从向量数据库中检索相关文档。
                通过生成用户问题的多个视角，你的目标是帮助用户克服基于距离的相似性搜索的一些局限性。
                请提供这些由换行符分隔的替代问题。原始问题是：
                {question}
                """);
        String question = documents.stream().map(Document::getContent).collect(Collectors.joining("\n"));
        AiMessageResponse aiResp = prompt.invoke(llm, question);

        Pattern reg = Pattern.compile("^\\d+\\s*\\..*");
        Predicate<String> p = reg.asPredicate();

        return Arrays.stream(aiResp.getMessage().getContent().toString().split("\n"))
                .filter(p)
                .map(Document::of).toList();
    }
}
