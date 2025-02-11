
package com.uu2.tinyagents.core.tools.function;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Parameter implements Serializable {

    protected String name;
    protected String type;
    protected String description;
    protected String[] enums;
    protected boolean required = false;

}
