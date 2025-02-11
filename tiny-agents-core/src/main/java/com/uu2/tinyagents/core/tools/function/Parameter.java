
package com.uu2.tinyagents.core.tools.function;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter implements Serializable {

    protected String name;
    protected String type;
    protected String description;
    protected String[] enums;
    @Builder.Default
    protected boolean required = false;

}
