package com.uu2.tinyagents.core.audio.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAudioRequest extends BaseAudioRequest {
    private String prompt;
    private List<String> modalities;
    private Map<String, String> audio;
}
