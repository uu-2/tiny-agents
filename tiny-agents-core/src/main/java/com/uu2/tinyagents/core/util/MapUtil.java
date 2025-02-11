
package com.uu2.tinyagents.core.util;

import java.util.Map;
import java.util.function.Function;

public class MapUtil {
    private static final boolean IS_JDK8 = (8 == getJvmVersion0());

    private MapUtil() {
    }

    private static String tryTrim(String string) {
        return string != null ? string.trim() : "";
    }

    private static int getJvmVersion0() {
        int jvmVersion = -1;
        try {
            String javaSpecVer = tryTrim(System.getProperty("java.specification.version"));
            if (StringUtil.hasText(javaSpecVer)) {
                if (javaSpecVer.startsWith("1.")) {
                    javaSpecVer = javaSpecVer.substring(2);
                }
                if (javaSpecVer.indexOf('.') == -1) {
                    jvmVersion = Integer.parseInt(javaSpecVer);
                }
            }
        } catch (Throwable ignore) {
            // ignore
        }
        // default is jdk8
        if (jvmVersion == -1) {
            jvmVersion = 8;
        }
        return jvmVersion;
    }

    /**
     * A temporary workaround for Java 8 specific performance issue JDK-8161372 .<br>
     * This class should be removed once we drop Java 8 support.
     *
     * @see <a href=
     * "https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372</a>
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        if (IS_JDK8) {
            V value = map.get(key);
            if (value != null) {
                return value;
            }
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

}
