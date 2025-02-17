package com.uu2.tinyagents.audio.openai;

import com.alibaba.fastjson.JSON;
import com.uu2.tinyagents.core.audio.AudioModel;
import com.uu2.tinyagents.core.audio.transfer.AudioResponse;
import com.uu2.tinyagents.core.audio.transfer.Speech2TextRequest;
import com.uu2.tinyagents.core.audio.transfer.Text2SpeechRequest;
import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.util.IOUtil;
import com.uu2.tinyagents.core.util.Maps;
import com.uu2.tinyagents.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class OpenAIAudioModel implements AudioModel {
    private final OpenAIAudioModelConfig config;
    private final HttpClient httpClient = new HttpClient();

    public OpenAIAudioModel(OpenAIAudioModelConfig config) {
        this.config = config;
    }

    @Override
    public AudioResponse Text2Speech(Text2SpeechRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + config.getApiKey());

        String payload = Maps.of("model", config.getTtsModel())
                .set("input", request.getInput())
                .set("voice", request.getVoice())
                .setIfNotNull("response_format", request.getResponseFormat())
                .setIfNotNull("speed", request.getSpeed())
                .toJSON();


        String url = config.getEndpoint() + "/v1/audio/speech";
        byte[] responseBytes = httpClient.postBytes(url, headers, payload);

        if (responseBytes == null || responseBytes.length == 0) {
            return AudioResponse.error("response is empty");
        }

        return AudioResponse.builder()
                .text(request.getInput())
                .audio(responseBytes)
                .build();
    }

    @Override
    public AudioResponse Speech2Text(Speech2TextRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data");
        headers.put("Authorization", "Bearer " + config.getApiKey());

        Maps payload = Maps.of("model", config.getSttModel())
                .set("file", request.getFile())
                .setIfNotEmpty("language", request.getLanguage())
                .setIfNotNull("prompt", request.getPrompt())
                .setIfNotNull("temperature", request.getTemperature())
                .setIfNotNull("response_format", request.getResponseFormat());


        String url = config.getEndpoint() + "/v1/audio/transcriptions";
        String responseJson = httpClient.multipartString(url, headers, payload);

        if (StringUtil.noText(responseJson)) {
            return AudioResponse.error("response is no text");
        }

        String text = JSON.parseObject(responseJson).getString("text");

        return AudioResponse.builder()
                .text(text)
                .audio(IOUtil.readBytes(request.getFile()))
                .build();
    }
}
