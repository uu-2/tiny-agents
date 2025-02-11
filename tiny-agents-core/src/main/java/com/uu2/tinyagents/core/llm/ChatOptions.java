
package com.uu2.tinyagents.core.llm;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 每个大模型都可以有自己的实现类
 */
@Getter
@Setter
public class ChatOptions {

    public static final ChatOptions DEFAULT = new ChatOptions() {
        @Override
        public void setTemperature(Float temperature) {
            throw new IllegalStateException("Can not set temperature to the default instance.");
        }

        @Override
        public void setMaxTokens(Integer maxTokens) {
            throw new IllegalStateException("Can not set maxTokens to the default instance.");
        }
    };

    private String seed;
    private Float temperature = 0.8f;
    private Float topP;
    private Integer topK;
    private Integer maxTokens;
    private List<String> stop;

}
