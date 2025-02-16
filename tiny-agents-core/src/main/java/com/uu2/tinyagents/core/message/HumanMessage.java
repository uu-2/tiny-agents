package com.uu2.tinyagents.core.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HumanMessage extends Message {
    @JSONField(serialize = false)
    private Object toolChoice;

    public HumanMessage(Object content) {
        super(Role.USER.getRole(), content);
    }

    public static HumanMessage of(Object content) {
        return new HumanMessage(content);
    }
}
