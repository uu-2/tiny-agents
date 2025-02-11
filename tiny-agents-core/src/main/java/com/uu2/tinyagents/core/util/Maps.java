
package com.uu2.tinyagents.core.util;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Maps extends HashMap<String, Object> {

    public static Maps of() {
        return new Maps();
    }

    public static Maps of(String key, Object value) {
        Maps maps = Maps.of();
        maps.put(key, value);
        return maps;
    }

    public static Maps ofNotNull(String key, Object value) {
        return new Maps().setIfNotNull(key, value);
    }

    public static Maps ofNotEmpty(String key, Object value) {
        return new Maps().setIfNotEmpty(key, value);
    }

    public static Maps ofNotEmpty(String key, Maps value) {
        return new Maps().setIfNotEmpty(key, value);
    }


    public Maps set(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Maps setChild(String key, Object value) {
        if (key.contains(".")) {
            String[] keys = key.split("\\.");
            Map<String, Object> currentMap = this;
            for (int i = 0; i < keys.length; i++) {
                String currentKey = keys[i].trim();
                if (currentKey.isEmpty()) {
                    continue;
                }
                if (i == keys.length - 1) {
                    currentMap.put(currentKey, value);
                } else {
                    //noinspection unchecked
                    currentMap = (Map<String, Object>) currentMap.computeIfAbsent(currentKey, k -> Maps.of());
                }
            }
        } else {
            super.put(key, value);
        }

        return this;
    }

    public Maps setOrDefault(String key, Object value, Object orDefault) {
        if (isNullOrEmpty(value)) {
            return this.set(key, orDefault);
        } else {
            return this.set(key, value);
        }
    }

    public Maps setIf(boolean condition, String key, Object value) {
        if (condition) put(key, value);
        return this;
    }

    public Maps setIf(Function<Maps, Boolean> func, String key, Object value) {
        if (func.apply(this)) put(key, value);
        return this;
    }

    public Maps setIfNotNull(String key, Object value) {
        if (value != null) put(key, value);
        return this;
    }

    public Maps setIfNotEmpty(String key, Object value) {
        if (!isNullOrEmpty(value)) {
            put(key, value);
        }
        return this;
    }


    public Maps setIfContainsKey(String checkKey, String key, Object value) {
        if (this.containsKey(checkKey)) {
            this.put(key, value);
        }
        return this;
    }

    public Maps setIfNotContainsKey(String checkKey, String key, Object value) {
        if (!this.containsKey(checkKey)) {
            this.put(key, value);
        }
        return this;
    }

    public String toJSON() {
        return JSON.toJSONString(this);
    }


    private static boolean isNullOrEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
            return true;
        }

        if (value instanceof Map && ((Map<?, ?>) value).isEmpty()) {
            return true;
        }

        if (value.getClass().isArray() && Array.getLength(value) == 0) {
            return true;
        }

        return value instanceof String && ((String) value).trim().isEmpty();
    }
}
