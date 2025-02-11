
package com.uu2.tinyagents.core.convert;

public class BigIntegerConverter implements IConverter<java.math.BigInteger> {
    @Override
    public java.math.BigInteger convert(String text) {
        return new java.math.BigInteger(text);
    }
}
