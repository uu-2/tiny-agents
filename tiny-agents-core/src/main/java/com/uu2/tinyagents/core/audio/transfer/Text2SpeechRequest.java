package com.uu2.tinyagents.core.audio.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Text2SpeechRequest extends BaseAudioRequest {
    private String input;
    private String voice;
    private Float speed;
}
