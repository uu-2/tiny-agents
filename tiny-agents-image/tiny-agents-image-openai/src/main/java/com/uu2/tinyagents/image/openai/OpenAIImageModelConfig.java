
package com.uu2.tinyagents.image.openai;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OpenAIImageModelConfig implements Serializable {
    @Builder.Default
    private String endpoint = "https://api.openai.com";
    @Builder.Default
    private String model = "dall-e-3";
    private String apiKey;
}
