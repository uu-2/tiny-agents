package com.uu2.tinyagents.core.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuperBuilder
@Setter
public class Metadata implements Serializable {

    protected Map<String, Object> metadataMap;

    public Map<String, Object> getMetadataMap() {
        if (metadataMap == null) {
            return Collections.EMPTY_MAP;
        }
        Map<String , Object> clone = new HashMap<>(metadataMap);
        return clone;
    }

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