package com.ants.restful.matcher;

import java.util.Comparator;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-04-27
 */
public interface PathMatcher {

    /**
     * 判断字符串是否是正则表达式
     *
     * @param var1
     * @return
     */
    boolean isPattern(String var1);

    /**
     * 判断字符串是否满足正则表达式
     *
     * @param var1
     * @param var2
     * @return
     */
    boolean match(String var1, String var2);

    /**
     * 开始匹配字符
     *
     * @param var1
     * @param var2
     * @return
     */
    boolean matchStart(String var1, String var2);

    /**
     * 提取字符串
     *
     * @param var1
     * @param var2
     * @return
     */
    String extractPathWithinPattern(String var1, String var2);

    /**
     * QAQAQA
     *
     * @param var1
     * @param var2
     * @return
     */
    Map<String, String> extractUriTemplateVariables(String var1, String var2);

    /**
     * QAQAQA
     *
     * @param var1
     * @return
     */
    Comparator<String> getPatternComparator(String var1);

    /**
     * QAQAQA
     *
     * @param var1
     * @param var2
     * @return
     */
    String combine(String var1, String var2);
}
