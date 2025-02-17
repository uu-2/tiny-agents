package com.uu2.tinyagents.core.audio.transfer;

import com.uu2.tinyagents.core.util.Metadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioResponse extends Metadata {
    private String text;
    private byte[] audio;

    private boolean error;
    private String errorMessage;

    public static AudioResponse error(String errMessage) {
        AudioResponse response = new AudioResponse();
        response.setError(true);
        response.setErrorMessage(errMessage);
        return response;
    }
}