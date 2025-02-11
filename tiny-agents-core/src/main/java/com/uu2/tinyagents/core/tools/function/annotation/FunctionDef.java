
package com.uu2.tinyagents.core.tools.function.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FunctionDef {
    String name() default "";

    String description();

    boolean strict() default false;
}
