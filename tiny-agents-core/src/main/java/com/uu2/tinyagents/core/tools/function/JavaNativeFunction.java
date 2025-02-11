
package com.uu2.tinyagents.core.tools.function;

import com.uu2.tinyagents.core.convert.ConvertService;
import com.uu2.tinyagents.core.tools.function.annotation.FunctionDef;
import com.uu2.tinyagents.core.tools.function.annotation.FunctionParam;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class JavaNativeFunction extends Function {

    @Setter
    private Class<?> clazz;
    @Setter
    private Object object;
    private Method method;

    public void setMethod(Method method) {
        this.method = method;

        FunctionDef functionDef = method.getAnnotation(FunctionDef.class);
        this.name = functionDef.name();
        this.description = functionDef.description();

        List<JavaNativeParameter> parameterList = new ArrayList<>();
        java.lang.reflect.Parameter[] methodParameters = method.getParameters();
        for (java.lang.reflect.Parameter methodParameter : methodParameters) {
            JavaNativeParameter parameter = getParameter(methodParameter);
            parameterList.add(parameter);
        }
        this.parameters = parameterList.toArray(new JavaNativeParameter[]{});
    }

    @NotNull
    private static JavaNativeParameter getParameter(java.lang.reflect.Parameter methodParameter) {
        FunctionParam functionParam = methodParameter.getAnnotation(FunctionParam.class);
        JavaNativeParameter parameter = new JavaNativeParameter();
        parameter.setName(functionParam.name());
        parameter.setDescription(functionParam.description());
        parameter.setType(methodParameter.getType().getSimpleName().toLowerCase());
        parameter.setTypeClass(methodParameter.getType());
        parameter.setRequired(functionParam.required());
        parameter.setEnums(functionParam.enums().length > 0 ? functionParam.enums() : null);
        return parameter;
    }

    public Object invoke(Map<String, Object> argsMap) {
        try {
            Object[] args = new Object[this.parameters.length];
            for (int i = 0; i < this.parameters.length; i++) {
                JavaNativeParameter parameter = (JavaNativeParameter) this.parameters[i];
                Object value = argsMap.get(parameter.getName());
                args[i] = ConvertService.convert(value, parameter.getTypeClass());
            }
            return method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
