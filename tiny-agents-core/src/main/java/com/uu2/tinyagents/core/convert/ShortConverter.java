
package com.uu2.tinyagents.core.convert;

public class ShortConverter implements IConverter<Short>{
    @Override
    public Short convert(String text) throws ConvertException {
        return Short.parseShort(text);
    }
}
