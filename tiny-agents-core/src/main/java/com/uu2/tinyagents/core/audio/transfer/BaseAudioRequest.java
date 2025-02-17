package com.uu2.tinyagents.core.audio.transfer;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseAudioRequest {
    private String model;
    @JSONField(name = "response_format")
    private String responseFormat;
}
