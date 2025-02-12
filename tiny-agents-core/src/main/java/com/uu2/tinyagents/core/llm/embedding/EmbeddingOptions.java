package com.uu2.tinyagents.core.llm.embedding;


import com.uu2.tinyagents.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmbeddingOptions {
    public static final EmbeddingOptions DEFAULT = new EmbeddingOptions(){
        @Override
        public void setModel(String model) {
            throw new IllegalStateException("Can not set modal to the default instance.");
        }
    };

    private String model;

    public String getModelOrDefault(String defaultModel) {
        return StringUtil.noText(model) ? defaultModel : model;
    }


    @Override
    public String toString() {
        return "EmbeddingOptions{" +
            "model='" + model + '\'' +
            '}';
    }
}
