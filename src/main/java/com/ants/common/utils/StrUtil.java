package com.ants.common.utils;

import java.util.UUID;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-04-27
 */
public class StrUtil {

    /**
     * 首字母变小写
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 字符串为 null 或者内部字符全部为 ' ' '\t' '\n' '\r' 这四类字符时返回 true
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            switch (str.charAt(i)) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    // case '\b':
                    // case '\f':
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串对象是否为Null
     *
     * @param strings
     * @return
     */
    public static boolean notBlank(String... strings) {
        if (strings == null) {
            return false;
        }
        for (String str : strings) {
            if (isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断对象是否为Null
     *
     * @param paras
     * @return
     */
    public static boolean notNull(Object... paras) {
        if (paras == null) {
            return false;
        }
        for (Object obj : paras) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }


    /**
     * 字符串数组中加入某个字符
     *
     * @param stringArray
     * @param separator
     * @return
     */
    public static String join(Object[] stringArray, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(stringArray[i]);
        }
        return sb.toString();
    }

    /**
     * 从第几个数组开始加入字符
     *
     * @param stringArray 对象数组
     * @param separator   需要加入的字符
     * @param num         加入的位置
     * @return
     */
    public static String join(Object[] stringArray, String separator, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < stringArray.length; i++) {
            sb.append(stringArray[i]);
            if (i < stringArray.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 获取32位随机数
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 根据倒数第几个字符截取字符串取最后一部分
     *
     * @param str       字符串
     * @param separator 截取字符
     * @param num       倒数开始第几个
     * @return
     */
    public static String lastSubstring(String str, char separator, int num) {
        return str.substring(getSubIndex(str, separator, num) + 1, str.length());
    }

    /**
     * 根据倒数第几个字符截取字符串取前面一部分
     *
     * @param str       字符串
     * @param separator 截取字符
     * @param num       倒数开始第几个
     * @return
     */
    public static String substring(String str, char separator, int num) {
        return str.substring(0, getSubIndex(str, separator, num));
    }

    private static int getSubIndex(String str, char separator, int num) {
        int j = 0, k = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            char[] chars = str.toCharArray();
            if (chars[i] == separator) {
                if (num - 1 == j) {
                    k = i;
                }
                j++;
            }
        }
        return k;
    }

    /**
     * 字符串首字母是否添加字符
     *
     * @param str       字符串
     * @param separator 字符
     * @return
     */
    public static String setFirstInitial(String str, Character separator) {
        char initial = str.toCharArray()[0];
        if (initial == separator) {
            return str;
        } else {
            return "/" + str;
        }
    }

    /**
     * 字符串首字母是否删除字符
     *
     * @param str
     * @param separator
     * @return
     */
    public static String delFirstInitial(String str, Character separator) {
        char initial = str.toCharArray()[0];
        if (initial != separator) {
            return str;
        } else {
            return str.substring(1);
        }
    }

    /**
     * 字符串尾部字母是否添加字符
     *
     * @param str       字符串
     * @param separator 字符
     * @return
     */
    public static String setLastInitial(String str, Character separator) {
        char initial = str.toCharArray()[str.length() - 1];
        if (initial == separator) {
            return str;
        } else {
            return str + separator;
        }
    }

    /**
     * 统计一个字符串出现的次数
     *
     * @param str     字符串
     * @param content 内容
     * @return
     */
    public static int count(String str, String content) {
        int count = 0;
        while (content.indexOf(str) != -1) {
            int i = content.indexOf(str);
            content = content.substring(i + 1, content.length() - 1);
            count++;
        }
        return count;
    }
}
