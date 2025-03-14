
package com.uu2.tinyagents.core.util;

public class StringUtil {

    public static boolean noText(String string) {
        return !hasText(string);
    }

    public static boolean hasText(String string) {
        return string != null && !string.isEmpty() && containsText(string);
    }

    public static boolean hasText(String... strings) {
        for (String string : strings) {
            if (!hasText(string)) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsText(CharSequence str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String obtainFirstHasText(String... strings) {
        for (String string : strings) {
            if (hasText(string)) {
                return string;
            }
        }
        return null;
    }

    public static boolean isJsonObject(String jsonString) {
        if (noText(jsonString)) {
            return false;
        }

        jsonString = jsonString.trim();
        return jsonString.startsWith("{") && jsonString.endsWith("}");
    }

    public static boolean notJsonObject(String jsonString) {
        return !isJsonObject(jsonString);
    }


}
