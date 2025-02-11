
package com.uu2.tinyagents.core.convert;

public class LongConverter  implements IConverter<Long> {
    @Override
    public Long convert(String text) {
        return Long.parseLong(text);
    }
}

