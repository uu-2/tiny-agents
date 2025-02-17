
package com.uu2.tinyagents.image.openai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uu2.tinyagents.core.image.ImageModel;
import com.uu2.tinyagents.core.image.transfer.EditImageRequest;
import com.uu2.tinyagents.core.image.transfer.GenerateImageRequest;
import com.uu2.tinyagents.core.image.transfer.ImageResponse;
import com.uu2.tinyagents.core.image.transfer.VaryImageRequest;
import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.util.Maps;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class OpenAIImageModel implements ImageModel {

    private OpenAIImageModelConfig config;
    private HttpClient httpClient = new HttpClient();

    public OpenAIImageModel(OpenAIImageModelConfig config) {
        this.config = config;
    }

    @Override
    public ImageResponse generate(GenerateImageRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + config.getApiKey());

        String payload = Maps.of("model", config.getModel())
                .set("prompt", request.getPrompt())
                .setIfNotNull("n", request.getN())
                .set("size", request.getSize())
                .toJSON();


        String url = config.getEndpoint() + "/v1/images/generations";
        String responseJson = httpClient.post(url, headers, payload);

        if (StringUtil.noText(responseJson)) {
            return ImageResponse.error("response is no text");
        }

        JSONObject root = JSON.parseObject(responseJson);
        JSONArray images = root.getJSONArray("data");
        if (images == null || images.isEmpty()) {
            return ImageResponse.error("image data is empty: " + responseJson);
        }
        ImageResponse response = new ImageResponse();
        for (int i = 0; i < images.size(); i++) {
            JSONObject imageObj = images.getJSONObject(i);
            response.addImage(imageObj.getString("url"));
        }

        return response;
    }


    @Override
    public ImageResponse edit(EditImageRequest request) {
        throw new UnsupportedOperationException("not support edit image");
    }

    @Override
    public ImageResponse vary(VaryImageRequest request) {
        throw new UnsupportedOperationException("not support vary image");
    }

}
