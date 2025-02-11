
package com.uu2.tinyagents.core.tools.function.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface FunctionParam {
    String name();

    String description();

    String[] enums() default {};

    boolean required() default false;

}
