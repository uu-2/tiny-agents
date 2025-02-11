
package com.uu2.tinyagents.core.convert;


import com.uu2.tinyagents.core.util.ArrayUtil;
import com.uu2.tinyagents.core.util.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ConvertService {

    private static final Map<Class<?>, IConverter<?>> CONVERTER_MAP = new HashMap<>();

    static {
        register(new BooleanConverter(), Boolean.class, boolean.class);
        register(new IntegerConverter(), Integer.class, int.class);
        register(new LongConverter(), Long.class, long.class);
        register(new DoubleConverter(), Double.class, double.class);
        register(new FloatConverter(), Float.class, float.class);
        register(new ShortConverter(), Short.class, short.class);

        register(new BigDecimalConverter(), BigDecimal.class);
        register(new BigIntegerConverter(), BigInteger.class);
        register(new ByteConverter(), byte.class);
        register(new ByteArrayConverter(), byte[].class);


    }

    private static void register(IConverter<?> converter, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            CONVERTER_MAP.put(clazz, converter);
        }
    }

    public static Object convert(Object value, Class<?> toType) {
        if (value == null || (value.getClass() == String.class && StringUtil.noText((String) value)
            && toType != String.class)) {
            return null;
        }

        if (value.getClass().isAssignableFrom(toType)) {
            return value;
        }

        if (toType == Serializable.class && ArrayUtil.contains(value.getClass().getInterfaces(), Serializable.class)) {
            return value;
        }

        String valueString = value.toString().trim();
        if (valueString.isEmpty()) {
            return null;
        }

        IConverter<?> converter = CONVERTER_MAP.get(toType);
        if (converter != null) {
            return converter.convert(valueString);
        }

        return null;
    }

    public static Object getPrimitiveDefaultValue(Class<?> paraClass) {
        if (paraClass == int.class || paraClass == long.class || paraClass == float.class || paraClass == double.class) {
            return 0;
        } else if (paraClass == boolean.class) {
            return Boolean.FALSE;
        } else if (paraClass == short.class) {
            return (short) 0;
        } else if (paraClass == byte.class) {
            return (byte) 0;
        } else if (paraClass == char.class) {
            return '\u0000';
        } else {
            //不存在这种类型
            return null;
        }
    }


}
