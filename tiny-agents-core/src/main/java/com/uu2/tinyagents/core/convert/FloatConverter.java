
package com.uu2.tinyagents.core.convert;

public class FloatConverter implements IConverter<Float> {
    @Override
    public Float convert(String text) {
        return Float.parseFloat(text);
    }
}
