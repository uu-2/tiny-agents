
package com.uu2.tinyagents.core.tools.function;


import com.uu2.tinyagents.core.tools.function.annotation.FunctionDef;
import com.uu2.tinyagents.core.util.ArrayUtil;
import com.uu2.tinyagents.core.util.ClassUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class JavaNativeFunctions extends ArrayList<Function> {

    public static JavaNativeFunctions from(Object object, String... methodNames) {
        return from(object.getClass(), object, methodNames);
    }

    public static JavaNativeFunctions from(Class<?> clazz, String... methodNames) {
        return from(clazz, null, methodNames);
    }

    private static JavaNativeFunctions from(Class<?> clazz, Object object, String... methodNames) {
        clazz = ClassUtil.getUsefulClass(clazz);
        List<Method> methodList = ClassUtil.getAllMethods(clazz, method -> {
            if (object == null && !Modifier.isStatic(method.getModifiers())) {
                return false;
            }
            if (method.getAnnotation(FunctionDef.class) == null) {
                return false;
            }
            if (methodNames.length > 0) {
                return ArrayUtil.contains(methodNames, method.getName());
            }
            return true;
        });

        JavaNativeFunctions javaNativeFunctions = new JavaNativeFunctions();

        for (Method method : methodList) {
            JavaNativeFunction function = new JavaNativeFunction();
            function.setClazz(clazz);
            function.setMethod(method);

            if (!Modifier.isStatic(method.getModifiers())) {
                function.setObject(object);
            }

            javaNativeFunctions.add(function);
        }

        return javaNativeFunctions;
    }


}
