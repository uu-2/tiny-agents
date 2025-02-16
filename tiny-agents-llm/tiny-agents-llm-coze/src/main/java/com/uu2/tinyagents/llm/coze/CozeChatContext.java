
package com.uu2.tinyagents.llm.coze;


import com.uu2.tinyagents.core.llm.ChatContext;
import com.uu2.tinyagents.core.message.AiMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Setter
@Getter
public class CozeChatContext extends ChatContext {

    private String id;
    private String conversationId;

    private String botId;

    private String status;

    private long createdAt;

    private Map lastError;

    private Map usage;

    private AiMessage message;

    private String response;


}
