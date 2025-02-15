
package com.uu2.tinyagents.core.image;

import com.uu2.tinyagents.core.image.transfer.EditImageRequest;
import com.uu2.tinyagents.core.image.transfer.GenerateImageRequest;
import com.uu2.tinyagents.core.image.transfer.ImageResponse;
import com.uu2.tinyagents.core.image.transfer.VaryImageRequest;

public interface ImageModel {

    ImageResponse generate(GenerateImageRequest request);

    ImageResponse edit(EditImageRequest request);

    ImageResponse vary(VaryImageRequest request);

}
