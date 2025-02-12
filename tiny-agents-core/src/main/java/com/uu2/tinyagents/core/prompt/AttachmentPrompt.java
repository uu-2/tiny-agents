package com.uu2.tinyagents.core.prompt;

import com.uu2.tinyagents.core.message.AttachmentMessage;
import com.uu2.tinyagents.core.message.Message;
import com.uu2.tinyagents.core.message.SystemMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class AttachmentPrompt extends Prompt {

    private SystemMessage systemMessage;
    AttachmentMessage attachmentMessage;

    public AttachmentPrompt(AttachmentMessage attachmentMessage) {
        this.attachmentMessage = attachmentMessage;
    }

    public void addAttachments(AttachmentMessage.Attachment... attachments) {
        if (attachments == null) {
            return;
        }
        for (AttachmentMessage.Attachment attachment : attachments) {
            attachmentMessage.addAttachment(attachment);
        }
    }

    @Override
    public List<Message> messages() {
        if (getSystemMessage() != null) {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(getSystemMessage());
            messages.add(attachmentMessage);
            return messages;
        }
        return Collections.singletonList(attachmentMessage);
    }
}
