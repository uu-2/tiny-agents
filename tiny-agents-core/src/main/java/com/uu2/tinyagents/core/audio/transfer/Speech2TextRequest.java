package com.uu2.tinyagents.core.audio.transfer;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Speech2TextRequest extends BaseAudioRequest {
    private File file;
    private String language;
    @JSONField(name = "response_format")
    private String responseFormat;
    private Float temperature;
    private String prompt;
}
