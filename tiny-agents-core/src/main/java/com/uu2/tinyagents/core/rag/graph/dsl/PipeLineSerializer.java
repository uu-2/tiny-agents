package com.uu2.tinyagents.core.rag.graph.dsl;

import com.alibaba.fastjson.JSON;
import com.uu2.tinyagents.core.rag.graph.Edge;
import com.uu2.tinyagents.core.rag.graph.Node;
import com.uu2.tinyagents.core.rag.graph.StateGraph;

import java.util.List;
import java.util.Map;

public class PipeLineSerializer {

    public static String serialize(StateGraph stateGraph) {

        Map<String, Node> nodes = stateGraph.getNodes();
        Map<String, Edge> edges = stateGraph.getEdges();

        List<NodeDef> nodeDefs = nodes.values().stream().map(NodeDef::new).toList();
        List<EdgeDef> edgeDefs = edges.values().stream().map(EdgeDef::new).toList();

        return JSON.toJSONString(
                new GraphDef(
                        nodeDefs,
                        edgeDefs
                )
        );
    }

    public static StateGraph deserialize(String raw) {
        GraphDef graphDef = JSON.parseObject(raw, GraphDef.class);;

        try {
            StateGraph graph = new StateGraph();

            for (NodeDef nodeDef : graphDef.getNodeDefs()) {
                Node node = deserialize(nodeDef);
                graph.add(node);
            }

            for (EdgeDef edgeDef : graphDef.getEdgeDefs()) {
                Edge edge = deserialize(edgeDef);
                graph.add(edge);
            }

            return graph;
        } catch (Exception e) {
            throw new RuntimeException("ERROR: ", e);
        }
    }

    private static Edge deserialize(EdgeDef edgeDef) {
        if (edgeDef == null) {
            return null;
        }
        try {
            Edge edge = (Edge) Class.forName(edgeDef.getType()).getDeclaredConstructor().newInstance();
            edge.decode(edgeDef.getContent());
            return edge;
        } catch (Exception e) {
            throw new RuntimeException("PipelineSerializer.deserialize(EdgeDef) ERROR: " + edgeDef.getType(), e);
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
            return node;
        } catch (Exception e) {
            throw new RuntimeException("PipelineSerializer.deserialize(NodeDef) ERROR: " + nodeDef.getType(), e);
        }
    }

}
