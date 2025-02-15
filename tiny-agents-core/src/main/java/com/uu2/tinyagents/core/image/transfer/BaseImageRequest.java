
package com.uu2.tinyagents.core.image.transfer;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class BaseImageRequest {
    private String model;
    private Integer n;
    private String responseFormat;
    private String user;
    private Integer width;
    private Integer height;
    private Map<String, Object> options;

    public void setSize(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    public String getSize() {
        if (this.width == null || this.height == null) {
            return null;
        }
        return this.width + "x" + this.height;
    }


    public void addOption(String key, Object value) {
        if (this.options == null) {
            this.options = new HashMap<>();
        }
        this.options.put(key, value);
    }

    public Object getOption(String key) {
        return this.options == null ? null : this.options.get(key);
    }

    public Object getOptionOrDefault(String key, Object defaultValue) {
        return this.options == null ? defaultValue : this.options.getOrDefault(key, defaultValue);
    }
}
