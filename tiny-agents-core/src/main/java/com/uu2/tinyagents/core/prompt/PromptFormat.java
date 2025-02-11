package com.uu2.tinyagents.core.prompt;

public interface PromptFormat {
    Object messagesFormat(Prompt prompt);

    Object toolsFormat(Prompt prompt);
}
