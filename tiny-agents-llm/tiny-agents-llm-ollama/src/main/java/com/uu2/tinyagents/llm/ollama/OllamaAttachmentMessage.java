package com.uu2.tinyagents.llm.ollama;

import com.uu2.tinyagents.core.message.AttachmentMessage;
import com.uu2.tinyagents.core.message.Message;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OllamaAttachmentMessage extends AttachmentMessage {
    public OllamaAttachmentMessage(String content) {
        super(content);
    }


    @Override
    public Object prompt() {
        return new MessageWrapper(this.getContent(), this.getAttachments());
    }

    @Getter
    public static class MessageWrapper extends Message {

        private List<String> images;

        public MessageWrapper(Object content, List<Attachment> attachments) {
            super(Role.USER.getRole(), content);
            if (attachments != null && !attachments.isEmpty()) {
                images = new LinkedList<>();
                attachments.forEach(attachment -> {
                    if (attachment instanceof ImageUrl imageUrl) {
                        images.add(imageUrl.encodeBase64());
                    }
                });
            }
        }
    }
}
