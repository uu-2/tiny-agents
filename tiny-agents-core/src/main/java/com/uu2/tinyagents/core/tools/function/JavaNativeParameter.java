
package com.uu2.tinyagents.core.tools.function;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JavaNativeParameter extends Parameter {

    protected Class<?> typeClass;

    JavaNativeParameter() {
        super();
    }
}
