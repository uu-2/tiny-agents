package com.uu2.tinyagents.core.audio.transfer;

import com.uu2.tinyagents.core.util.Metadata;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class AudioResponse extends Metadata {
    private String text;
    private byte[] audio;

    private boolean error;
    private String errorMessage;

    public static AudioResponse error(String errMessage) {
        return AudioResponse.builder()
                .error(true)
                .errorMessage(errMessage)
                .build();
    }
}