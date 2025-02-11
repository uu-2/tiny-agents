
package com.uu2.tinyagents.core.util;

import java.util.Collection;
import java.util.List;

public class CollectionUtil {

    private CollectionUtil() {
    }


    public static <T> boolean noItems(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }


    public static boolean hasItems(Collection<?> collection) {
        return !noItems(collection);
    }

    public static <T> T firstItem(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
    

    public static <T> T lastItem(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }
}
