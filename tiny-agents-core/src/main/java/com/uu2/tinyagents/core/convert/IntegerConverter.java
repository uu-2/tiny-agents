
package com.uu2.tinyagents.core.convert;

public class IntegerConverter implements IConverter<Integer>{
    @Override
    public Integer convert(String text) throws ConvertException {
        return Integer.parseInt(text);
    }
}
