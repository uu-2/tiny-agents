package com.uu2.tinyagents.core.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: three3q
 * @date: 2025/02/12
 * @description: 推荐格式：
 *      <Role：角色定义><背景目标>
*       <Input：输入>
 *      <Output：输出><Constraints: 约束条件>
 *      <Examples：示例参考可选>
 */
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
