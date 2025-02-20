package com.uu2.tinyagents.core.rag.document;

import com.uu2.tinyagents.core.util.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Document extends Metadata {
    private String content;
    private String id;
    private double[] vector;
    
}
