package com.uu2.tinyagents.audio.openai;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder

public class OpenAIAudioModelConfig implements Serializable {
    @Builder.Default
    private String endpoint = "https://api.openai.com";
    @Builder.Default
    private String ttsModel = "tts-1"; // text-to-speech: tts-1 or tts-1-hd
    @Builder.Default
    private String sttModel = "whisper-1"; // speech-to-text: whisper-1 or whisper-1-hd
    private String apiKey;
}
