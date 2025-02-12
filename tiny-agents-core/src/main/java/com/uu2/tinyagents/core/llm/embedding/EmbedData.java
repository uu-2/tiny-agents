
package com.uu2.tinyagents.core.llm.embedding;


import com.uu2.tinyagents.core.util.Metadata;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;


@Setter
@Getter
public class EmbedData extends Metadata {

    private double[][] vector;


    @Override
    public String toString() {
        return "VectorData{" +
                ", vector=" + Arrays.toString(vector) +
                '}';
    }
}
