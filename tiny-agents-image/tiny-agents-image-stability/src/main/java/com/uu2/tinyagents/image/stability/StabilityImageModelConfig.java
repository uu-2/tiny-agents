
package com.uu2.tinyagents.image.stability;

public class StabilityImageModelConfig {
    private String endpoint = "https://api.stability.ai/";
    private String apiKey;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
