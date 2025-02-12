package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.memory.ChatMemory;
import com.uu2.tinyagents.core.memory.DefaultChatMemory;
import com.uu2.tinyagents.core.message.AttachmentMessage;
import lombok.Getter;

@Getter
public class AttachmentPrompt extends HistoriesPrompt {

    AttachmentMessage attachmentMessage;

    public AttachmentPrompt(ChatMemory memory) {
        super(memory);
    }

    public AttachmentPrompt(AttachmentMessage attachmentMessage) {
        super(new DefaultChatMemory());
        this.attachmentMessage = attachmentMessage;
        this.addMessage(attachmentMessage);
    }

    public void setAttachmentMessage(AttachmentMessage attachmentMessage) {
        this.attachmentMessage = attachmentMessage;
        this.addMessage(attachmentMessage);
    }

    public void addAttachments(AttachmentMessage.Attachment... attachments) {
        if (attachments == null) {
            return;
        }
        for (AttachmentMessage.Attachment attachment : attachments) {
            attachmentMessage.addAttachment(attachment);
        }
    }

}
