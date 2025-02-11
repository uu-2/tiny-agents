
package com.uu2.tinyagents.core.llm;


import com.uu2.tinyagents.core.message.AiMessage;

public interface MessageResponse<M extends AiMessage> {
    M getMessage();
}
