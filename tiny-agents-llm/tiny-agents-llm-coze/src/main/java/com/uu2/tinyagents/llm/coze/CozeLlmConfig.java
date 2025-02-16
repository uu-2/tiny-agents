
package com.uu2.tinyagents.llm.coze;


import com.uu2.tinyagents.core.llm.LlmConfig;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CozeLlmConfig extends LlmConfig {

    private final String DEFAULT_CHAT_API = "/v3/chat";
    private final String DEFAULT_ENDPOINT = "https://api.coze.cn";

    private String chatApi;


    private String defaultBotId;

    private String defaultConversationId;

    private String defaultUserId;

    private boolean stream;

    private boolean autoSaveHistory;

    public CozeLlmConfig() {
        this.setChatApi(DEFAULT_CHAT_API);
        this.setEndpoint(DEFAULT_ENDPOINT);
    }
}
