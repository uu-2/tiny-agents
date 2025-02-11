
package com.uu2.tinyagents.core.memory;

import java.util.Map;

public interface ContextMemory extends Memory {

    Object get(String key);

    Map<String, Object> getAll();

    void put(String key, Object value);

    void putAll(Map<String, Object> context);

    void remove(String key);

    void clear();
}
