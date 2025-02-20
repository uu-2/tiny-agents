
package com.uu2.tinyagents.core.llm.embedding;


import com.uu2.tinyagents.core.util.Metadata;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;


@Data
@SuperBuilder
public class EmbedData extends Metadata {

    private double[][] vector;


    @Override
    public String toString() {
        return "VectorData{" +
                ", vector=" + Arrays.toString(vector) +
                '}';
    }
}
