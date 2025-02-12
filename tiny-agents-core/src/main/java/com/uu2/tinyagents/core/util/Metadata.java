package com.uu2.tinyagents.core.util;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Metadata implements Serializable {

    protected Map<String, Object> metadataMap;

    public Object getMetadata(String key) {
        return metadataMap != null ? metadataMap.get(key) : null;
    }

    public void addMetadata(String key, Object value) {
        if (metadataMap == null) {
            metadataMap = new HashMap<>();
        }
        metadataMap.put(key, value);
    }

    public void addMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return;
        }
        if (metadataMap == null) {
            metadataMap = new HashMap<>();
        }
        metadataMap.putAll(metadata);
    }

    public Object removeMetadata(String key) {
        if (this.metadataMap == null) {
            return null;
        }
        return this.metadataMap.remove(key);
    }

    public void clearMetadata() {
        if (this.metadataMap != null) {
            this.metadataMap.clear();
        }
    }
}