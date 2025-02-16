package com.uu2.tinyagents.core.message.tools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class AiFunctionCall implements Serializable {
    private Integer index;
    @JSONField(name = "call_id")
    private String callId;
    private String type;
    private String name;
    private Map<String, Object> args;

    public AiFunctionCall(AiFunctionCall aiFunctionCall) {
        this.index = aiFunctionCall.index;
        this.callId = aiFunctionCall.callId;
        this.type = aiFunctionCall.type;
        this.name = aiFunctionCall.name;
        this.args = new HashMap<>(aiFunctionCall.args);
    }
}
