package com.ants.restful.matcher;


import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-04-27
 */
public class StringUtils {

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return null;
        } else {
            StringTokenizer st = new StringTokenizer(str, delimiters);
            ArrayList tokens = new ArrayList();

            while (true) {
                String token;
                do {
                    if (!st.hasMoreTokens()) {
                        return toStringArray((Collection) tokens);
                    }

                    token = st.nextToken();
                    if (trimTokens) {
                        token = token.trim();
                    }
                } while (ignoreEmptyTokens && token.length() <= 0);

                tokens.add(token);
            }
        }
    }

    public static String[] toStringArray(Collection<String> collection) {
        return collection == null ? null : (String[]) collection.toArray(new String[collection.size()]);
    }

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        } else {
            int strLen = str.length();

            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }


    /**
     * Convenience method to return a String array as a delimited (e.g. CSV)
     * String. E.g. useful for {@code toString()} implementations.
     *
     * @param arr   the array to display
     * @param delim the delimiter to use (probably a ",")
     * @return the delimited String
     */
    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        if (arr.length == 1) {
            return nullSafeToString(arr[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String nullSafeToString(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Object[]) {
            return nullSafeToString((Object[]) ((Object[]) obj));
        } else if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[]) ((boolean[]) obj));
        } else if (obj instanceof byte[]) {
            return nullSafeToString((byte[]) ((byte[]) obj));
        } else if (obj instanceof char[]) {
            return nullSafeToString((char[]) ((char[]) obj));
        } else if (obj instanceof double[]) {
            return nullSafeToString((double[]) ((double[]) obj));
        } else if (obj instanceof float[]) {
            return nullSafeToString((float[]) ((float[]) obj));
        } else if (obj instanceof int[]) {
            return nullSafeToString((int[]) ((int[]) obj));
        } else if (obj instanceof long[]) {
            return nullSafeToString((long[]) ((long[]) obj));
        } else if (obj instanceof short[]) {
            return nullSafeToString((short[]) ((short[]) obj));
        } else {
            String str = obj.toString();
            return str != null ? str : "";
        }
    }

    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }
}
