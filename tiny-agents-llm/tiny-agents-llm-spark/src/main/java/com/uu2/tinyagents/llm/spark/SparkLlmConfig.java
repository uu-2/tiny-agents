
package com.uu2.tinyagents.llm.spark;

import com.uu2.tinyagents.core.llm.LlmConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SparkLlmConfig extends LlmConfig {

    private String appId;
    private String apiSecret;
    private  String apiKey ;
    private  String version = "v3.5";


}
