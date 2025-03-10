
package com.uu2.tinyagents.core.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 类实例创建者创建者
 *
 */
public class ClassUtil {

    private ClassUtil() {
    }

    private static final String[] OBJECT_METHODS = new String[]{
        "toString",
        "getClass",
        "equals",
        "hashCode",
        "wait",
        "notify",
        "notifyAll",
        "clone",
        "finalize"
    };

    //proxy frameworks
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.Factory"
        // cglib
        , "org.springframework.cglib.proxy.Factory"

        // javassist
        , "javassist.util.proxy.ProxyObject"
        , "org.apache.ibatis.javassist.util.proxy.ProxyObject");
    private static final String ENHANCER_BY = "$$EnhancerBy";
    private static final String JAVASSIST_BY = "_$$_";

    public static boolean isProxy(Class<?> clazz) {
        for (Class<?> cls : clazz.getInterfaces()) {
            if (PROXY_CLASS_NAMES.contains(cls.getName())) {
                return true;
            }
        }
        //java proxy
        return Proxy.isProxyClass(clazz);
    }

    private static <T> Class<T> getJdkProxySuperClass(Class<T> clazz) {
        final Class<?> proxyClass = Proxy.getProxyClass(clazz.getClassLoader(), clazz.getInterfaces());
        return (Class<T>) proxyClass.getInterfaces()[0];
    }

    public static <T> Class<T> getUsefulClass(Class<T> clazz) {
        if (isProxy(clazz)) {
            return getJdkProxySuperClass(clazz);
        }

        //ControllerTest$ServiceTest$$EnhancerByGuice$$40471411#hello   -------> Guice
        //com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158  ----> CGLIB
        //io.jboot.test.app.TestAppListener_$$_jvstb9f_0 ------> javassist
        final String name = clazz.getName();
        if (name.contains(ENHANCER_BY) || name.contains(JAVASSIST_BY)) {
            return (Class<T>) clazz.getSuperclass();
        }

        return clazz;
    }


    public static Class<?> getWrapType(Class<?> clazz) {
        if (clazz == null || !clazz.isPrimitive()) {
            return clazz;
        }
        if (clazz == Integer.TYPE) {
            return Integer.class;
        } else if (clazz == Long.TYPE) {
            return Long.class;
        } else if (clazz == Boolean.TYPE) {
            return Boolean.class;
        } else if (clazz == Float.TYPE) {
            return Float.class;
        } else if (clazz == Double.TYPE) {
            return Double.class;
        } else if (clazz == Short.TYPE) {
            return Short.class;
        } else if (clazz == Character.TYPE) {
            return Character.class;
        } else if (clazz == Byte.TYPE) {
            return Byte.class;
        } else if (clazz == Void.TYPE) {
            return Void.class;
        }
        return clazz;
    }


    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray()
            || clazz == int[].class
            || clazz == long[].class
            || clazz == short[].class
            || clazz == float[].class
            || clazz == double[].class;
    }


    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        doGetFields(clazz, fields, null, false);
        return fields;
    }

    public static List<Field> getAllFields(Class<?> clazz, Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        doGetFields(clazz, fields, predicate, false);
        return fields;
    }

    public static Field getFirstField(Class<?> clazz, Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        doGetFields(clazz, fields, predicate, true);
        return fields.isEmpty() ? null : fields.get(0);
    }

    private static void doGetFields(Class<?> clazz, List<Field> fields, Predicate<Field> predicate, boolean firstOnly) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (predicate == null || predicate.test(declaredField)) {
                fields.add(declaredField);
                if (firstOnly) {
                    break;
                }
            }
        }

        if (firstOnly && !fields.isEmpty()) {
            return;
        }

        doGetFields(clazz.getSuperclass(), fields, predicate, firstOnly);
    }

    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(clazz, methods, null, false);
        return methods;
    }

    public static List<Method> getAllMethods(Class<?> clazz, Predicate<Method> predicate) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(clazz, methods, predicate, false);
        return methods;
    }

    public static Method getFirstMethod(Class<?> clazz, Predicate<Method> predicate) {
        List<Method> methods = new ArrayList<>();
        doGetMethods(clazz, methods, predicate, true);
        return methods.isEmpty() ? null : methods.get(0);
    }


    private static void doGetMethods(Class<?> clazz, List<Method> methods, Predicate<Method> predicate, boolean firstOnly) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (predicate == null || predicate.test(method)) {
                methods.add(method);
                if (firstOnly) {
                    break;
                }
            }
        }

        if (firstOnly && !methods.isEmpty()) {
            return;
        }

        doGetMethods(clazz.getSuperclass(), methods, predicate, firstOnly);
    }

}
