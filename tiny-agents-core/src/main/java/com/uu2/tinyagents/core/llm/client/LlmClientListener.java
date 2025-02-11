
package com.uu2.tinyagents.core.llm.client;

public interface LlmClientListener {

    void onStart(LlmClient client);

    void onMessage(LlmClient client,String response);

    void onStop(LlmClient client);

    void onFailure(LlmClient client, Throwable throwable);

}
