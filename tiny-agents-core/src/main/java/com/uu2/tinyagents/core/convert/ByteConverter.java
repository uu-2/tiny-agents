
package com.uu2.tinyagents.core.convert;

public class ByteConverter  implements IConverter<Byte> {
    @Override
    public Byte convert(String text) {
        return Byte.parseByte(text);
    }
}
