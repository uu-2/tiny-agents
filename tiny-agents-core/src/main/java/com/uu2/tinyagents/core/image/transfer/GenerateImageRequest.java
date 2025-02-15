
package com.uu2.tinyagents.core.image.transfer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenerateImageRequest extends BaseImageRequest {
    private String prompt;
    private String negativePrompt;
    private String quality;
    private String style;

}

