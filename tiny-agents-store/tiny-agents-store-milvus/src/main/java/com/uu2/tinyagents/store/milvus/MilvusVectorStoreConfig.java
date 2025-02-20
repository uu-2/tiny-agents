package com.uu2.tinyagents.store.milvus;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MilvusVectorStoreConfig {
    private String collectionName;
    private String partitionName;
    @Builder.Default
    private boolean autoCreateCollection = true;
}
