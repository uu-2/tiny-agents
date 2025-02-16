package com.uu2.tinyagents.core.message;

import com.alibaba.fastjson2.annotation.JSONField;
import com.uu2.tinyagents.core.util.ImageUtil;
import com.uu2.tinyagents.core.util.StringUtil;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Getter
@Setter
public class AttachmentMessage extends Message {
    @JSONField(serialize = false)
    private List<Attachment> attachments;

    public AttachmentMessage(Object content) {
        super(Role.USER.getRole(), content);
        attachments = new ArrayList<>();
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public static AttachmentMessage of(String content) {
        return new AttachmentMessage(content);
    }

    @Override
    public Object prompt() {
        List<Attachment> res = new ArrayList<>();
        res.add(Text.builder().text(this.getContent()).build());
        res.addAll(this.attachments);
        return HumanMessage.of(res);
    }

    public interface Attachment extends Serializable {
        String encodeBase64();
    }

    @Builder
    @Data
    public static class Text implements Attachment {
        @Builder.Default
        private String type = "text";
        private Object text;

        @Override
        public String encodeBase64() {
            return Base64.getEncoder().encodeToString(String.valueOf(text).getBytes());
        }
    }

    @Builder
    @Data
    public static class ImageUrl implements Attachment {
        @Builder.Default
        private String type = "image_url";
        @JSONField(name = "image_url")
        private ImageUrdDef imageUrl;
        @JSONField(serialize = false)
        private String base64;

        @Override
        public String encodeBase64() {
            if (StringUtil.noText(base64)) {
                base64 = ImageUtil.imageUrlToBase64(imageUrl.getUrl());
            }
            return base64;
        }

        @Data
        public static class ImageUrdDef {
            private String url;
            private String detail;

            public ImageUrdDef(String url) {
                this(url, null);
            }

            public ImageUrdDef(String url, String detail) {
                this.url = url;
                this.detail = detail;
            }
        }
    }
}
