
package com.uu2.tinyagents.core.convert;

public class ByteArrayConverter implements IConverter<byte[]> {
    @Override
    public byte[] convert(String text) {
        return text.getBytes();
    }
}
