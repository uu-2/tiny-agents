
package com.uu2.tinyagents.llm.coze;

public interface CozeRequestListener {

    void onMessage(CozeChatContext context);

    void onFailure(CozeChatContext context, Throwable throwable);

    void onStop(CozeChatContext context);
}
