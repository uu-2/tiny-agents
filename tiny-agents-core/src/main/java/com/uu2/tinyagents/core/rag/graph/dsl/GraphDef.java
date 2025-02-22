package com.uu2.tinyagents.core.rag.graph.dsl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphDef {
    List<NodeDef> nodeDefs;
    List<EdgeDef> edgeDefs;
}
