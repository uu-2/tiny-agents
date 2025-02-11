
package com.uu2.tinyagents.core.llm;

import com.uu2.tinyagents.core.llm.response.AiMessageResponse;
import org.slf4j.Logger;

public interface StreamResponseListener {

    Logger logger = org.slf4j.LoggerFactory.getLogger(StreamResponseListener.class);

    default void onStart(ChatContext context) {
    }

    void onMessage(ChatContext context, AiMessageResponse response);

    default void onStop(ChatContext context) {
    }

    default void onFailure(ChatContext context, Throwable throwable) {
        if (throwable != null) {
            logger.error(throwable.toString(), throwable);
        }
    }
}
