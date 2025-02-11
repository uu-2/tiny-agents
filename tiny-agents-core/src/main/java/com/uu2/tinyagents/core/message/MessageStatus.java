
package com.uu2.tinyagents.core.message;

import java.io.Serializable;

/**
 * 消息状态，用于在流式（stream）的场景下，用于标识当前消息的状态
 */
public enum MessageStatus implements Serializable {

    /**
     * 开始内容
     */
    START(1),

    /**
     * 中间内容
     */
    MIDDLE(2),

    /**
     * 结束内容，一般情况下指的是最后一条内容
     */
    END(3),

    /**
     * 其他内容
     */
    UNKNOW(9),
    ;
    private int value;

    MessageStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
