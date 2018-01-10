package cn.jants.common.utils;


/**
 * @author MrShun
 * @version 1.0
 */
public class StrCaseUtil {

    private static final char SEPARATOR = '_';

    /**
     * 字符串中所有大写字母转小写前面加_
     *
     * @param s
     * @return
     */
    public static String toUnderlineName(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i >= 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0) {
                        sb.append(SEPARATOR);
                    }
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 字符串中_后面小写字母转大写
     *
     * @param s
     * @return
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 删除字符串前缀
     *
     * @param prefixs 前缀
     * @param s       字符串
     * @return
     */
    public static String deletePrefix(String prefixs, String s) {
        try {
            for (String prefix : prefixs.split(",")) {
                int i = s.toUpperCase().indexOf(prefix.toUpperCase());
                if (i != -1) {
                    return s.substring(i + prefix.length(), s.length());
                }
            }
            return s;
        } catch (Exception e) {
            return s;
        }
    }

    /**
     * 字符串中_后面小写字母转大写, 首字母是否大写
     *
     * @param s       字符串
     * @param isFirst true 是
     * @return
     */
    public static String toCapital(String s, boolean isFirst) {
        if (s == null) {
            return null;
        }
        int i = s.indexOf(SEPARATOR);
        int index = 0;
        while (s.indexOf(SEPARATOR) != -1) {
            index = s.indexOf(SEPARATOR);
            if (index != 0 && index != s.length()) {
                s = s.substring(0, index) + s.substring(index + 1, index + 2).toUpperCase() + s.substring(index + 2, s.length());
            } else {
                return null;
            }
        }
        char[] cs = s.toCharArray();
        if (isFirst) {
            cs[0] -= 32;
        }
        return String.valueOf(cs);
    }
}