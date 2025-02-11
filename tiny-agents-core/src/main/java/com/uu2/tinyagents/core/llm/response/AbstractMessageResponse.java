
package com.uu2.tinyagents.core.llm.response;


import com.uu2.tinyagents.core.llm.MessageResponse;
import com.uu2.tinyagents.core.message.AiMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMessageResponse<M extends AiMessage> implements MessageResponse<M> {

    protected boolean error = false;
    protected String errorMessage;
    protected String errorType;
    protected String errorCode;

}
