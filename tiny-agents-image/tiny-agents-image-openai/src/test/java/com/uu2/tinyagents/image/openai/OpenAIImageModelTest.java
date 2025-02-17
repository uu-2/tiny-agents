
package com.uu2.tinyagents.image.openai;

import com.uu2.tinyagents.core.image.transfer.GenerateImageRequest;
import com.uu2.tinyagents.core.image.transfer.ImageResponse;
import org.junit.Test;

public class OpenAIImageModelTest {

    @Test
    public void testGenImage(){
        OpenAIImageModelConfig config = new OpenAIImageModelConfig();
        config.setApiKey("your key");

        OpenAIImageModel imageModel = new OpenAIImageModel(config);

        GenerateImageRequest request = new GenerateImageRequest();
        request.setPrompt("A cute little tiger standing in the high-speed train");
        ImageResponse generate = imageModel.generate(request);
        System.out.println(generate);
    }
}
