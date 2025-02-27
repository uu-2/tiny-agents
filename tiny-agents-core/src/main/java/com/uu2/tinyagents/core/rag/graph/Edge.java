package com.uu2.tinyagents.core.rag.graph;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Edge {
    private String from;
    private Condition cond;
    private Map<String, String> toNodes;

    public String invoke(ExecuteContext ctx, ExecuteResult fromNodeResult) {
        String v = cond.cond(ctx, fromNodeResult);
        return toNodes.getOrDefault(v, null);
    }

    public Object encode() {
        return Map.of(
                "from", from,
                "cond", Map.of(
                        "type", cond.getClass().getName(),
                        "content", cond.encode()
                ),
                "toNodes", toNodes
        );
    }

    public void decode(Object content) {
        JSONObject contentObject = (JSONObject) content;
        this.from = contentObject.getObject("from", String.class);
        this.toNodes = contentObject.getObject("toNodes", Map.class);

        try {
            JSONObject condDef = contentObject.getObject("cond", JSONObject.class);
            String condType = condDef.getObject("type", String.class);
            Condition cond = (Condition) Class.forName(condType).getDeclaredConstructor().newInstance();
            cond.decode(condDef.get("content"));
            this.cond = cond;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
