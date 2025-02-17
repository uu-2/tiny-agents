
package com.uu2.tinyagents.image.stability;

import com.uu2.tinyagents.core.image.ImageModel;
import com.uu2.tinyagents.core.image.transfer.EditImageRequest;
import com.uu2.tinyagents.core.image.transfer.GenerateImageRequest;
import com.uu2.tinyagents.core.image.transfer.ImageResponse;
import com.uu2.tinyagents.core.image.transfer.VaryImageRequest;
import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.util.Maps;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StabilityImageModel implements ImageModel {
    private static final Logger LOG = LoggerFactory.getLogger(StabilityImageModel.class);
    private StabilityImageModelConfig config;
    private HttpClient httpClient = new HttpClient();

    public StabilityImageModel(StabilityImageModelConfig config) {
        this.config = config;
    }

    @Override
    public ImageResponse generate(GenerateImageRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "image/*");
        headers.put("Authorization", "Bearer " + config.getApiKey());

        Map<String, Object> payload = Maps.of("prompt", request.getPrompt())
            .setIfNotNull("output_format", "jpeg");

        String url = config.getEndpoint() + "/v2beta/stable-image/generate/sd3";

        try (Response response = httpClient.multipart(url, headers, payload);
             ResponseBody body = response.body()) {
            if (response.isSuccessful() && body != null) {
                ImageResponse imageResponse = new ImageResponse();
                imageResponse.addImage(body.bytes());
                return imageResponse;
            }
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }

        return null;
    }

    @Override
    public ImageResponse edit(EditImageRequest request) {
        return null;
    }

    @Override
    public ImageResponse vary(VaryImageRequest request) {
        return null;
    }
}
