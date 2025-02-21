package com.uu2.tinyagents.core.rag.graph.dsl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uu2.tinyagents.core.rag.graph.Node;
import com.uu2.tinyagents.core.rag.graph.Pipeline;

public class PipeLineSerializer {

    public static String serialize(Pipeline pipeline) {
        return JSON.toJSONString(new NodeDef(pipeline.getHead()));
    }

    public static Pipeline deserialize(String json) {
        NodeDef nodeDef = JSON.parseObject(json, NodeDef.class);

        try {
            Node node = (Node) Class.forName(nodeDef.getType()).getDeclaredConstructor().newInstance();
            node.setId(nodeDef.getId());
            node.decode(nodeDef.getContent());
            node.setNext(deserialize(nodeDef.getNext()));

            return new Pipeline(node);
        } catch (Exception e) {
            throw new RuntimeException("ERROR: " + nodeDef.getType(), e);
        }
    }

    public static Node deserialize(NodeDef nodeDef) {
        if (nodeDef == null) {
            return null;
        }
        try {
            Node node = (Node) Class.forName(nodeDef.getType()).getDeclaredConstructor().newInstance();
            node.setId(nodeDef.getId());
            node.decode(nodeDef.getContent());
            node.setNext(deserialize(nodeDef.getNext()));
            return node;
        } catch (Exception e) {
            throw new RuntimeException("PipelineSerializer.deserialize() ERROR: " + nodeDef.getType(), e);
        }
    }

}
