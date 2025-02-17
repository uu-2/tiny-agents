package com.uu2.tinyagents.image.stability;

import com.uu2.tinyagents.core.image.transfer.GenerateImageRequest;
import com.uu2.tinyagents.core.image.transfer.ImageResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StabilityImageModelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGenImage(){
        StabilityImageModelConfig config = new StabilityImageModelConfig();
        config.setApiKey("sk-5gqOcl*****");

        StabilityImageModel imageModel = new StabilityImageModel(config);

        GenerateImageRequest request = new GenerateImageRequest();
        request.setPrompt("A cute little tiger standing in the high-speed train");
        ImageResponse generate = imageModel.generate(request);
        System.out.println(generate);
    }
}