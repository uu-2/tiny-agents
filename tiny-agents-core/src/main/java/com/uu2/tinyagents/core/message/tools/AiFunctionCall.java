package com.uu2.tinyagents.core.message.tools;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AiFunctionCall implements Serializable {
    private Integer index;
    @JSONField(name = "call_id")
    private String callId;
    private String name;
    private Map<String, Object> args;
}
