package com.uu2.tinyagents.core.rag;

import com.uu2.tinyagents.core.memory.ChatMemory;
import com.uu2.tinyagents.core.prompt.FunctionPrompt;
import com.uu2.tinyagents.core.prompt.template.PromptTemplate;

import java.util.Map;

public class RAGPrompt extends FunctionPrompt {
    public RAGPrompt(String content) {
        super(content);
    }

    public RAGPrompt(String content, ChatMemory memory) {
        super(content);
        this.setMemory(memory);
    }

    public RAGPrompt format(Map<String, Object> params) {
        /**
         * 1. retrieve（store）
         *      1.1. pre-search
         *          [压缩、重写、转换、扩展]
         *      1.1. do-search
         *          [vector、full text、graph]
         *          [link-search]
         *      1.2. post-search
         *          [rerank]
         * 2. generate（prompt）
         */
        return this;
    }
}
