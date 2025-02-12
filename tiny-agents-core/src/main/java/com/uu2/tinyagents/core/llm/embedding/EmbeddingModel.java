
package com.uu2.tinyagents.core.llm.embedding;


public interface EmbeddingModel {

    default EmbedData embed(String... documents) {
        return embed(EmbeddingOptions.DEFAULT, documents);
    }

    EmbedData embed(EmbeddingOptions options, String... documents);

    default int dimensions() {
        return embed("dimensions").getVector()[0].length;
    }
}
