
package com.uu2.tinyagents.llm.coze;


import com.uu2.tinyagents.core.llm.ChatOptions;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Setter
@Getter
public class CozeChatOptions extends ChatOptions {

    private String botId;

    private String conversationId;

    private String userId;

    private boolean stream;

    private Map<String, String> customVariables;

}
