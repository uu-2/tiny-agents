
package com.uu2.tinyagents.core.image.transfer;

import com.uu2.tinyagents.core.image.Image;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EditImageRequest extends GenerateImageRequest {
    private Image image;
    private Image mask;

}

