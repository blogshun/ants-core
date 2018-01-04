package com.ants.common.utils;

import com.ants.common.enums.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则判断工具类
 *
 * @author MrShun
 * @version 1.0
 * Date 2017-08-29
 */
public class RegexUtil {

    /**
     * 判断字段是否为空 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static synchronized boolean strIsNull(String str) {
        return null == str || str.trim().length() <= 0 ? true : false;
    }

    /**
     * 判断字段是非空 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean strNotNull(String str) {
        return !strIsNull(str);
    }

    /**
     * 字符串null转空
     *
     * @param str 字符串
     * @return boolean
     */
    public static String nullToStr(String str) {
        return strIsNull(str) ? "" : str;
    }

    /**
     * 字符串null赋值默认值
     *
     * @param str    目标字符串
     * @param defaultVal 默认值
     * @return String
     */
    public static String nullToStr(String str, String defaultVal) {
        return strIsNull(str) ? defaultVal : str;
    }

    /**
     * 判断字段是否为Email 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isEmail(String str) {
        return Regular(str, Regex.EMAIL.getRegex());
    }

    /**
     * 判断是否为电话号码 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isPhone(String str) {
        return Regular(str, Regex.PHONE.getRegex());
    }

    /**
     * 判断是否为手机号码 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isMobile(String str) {
        return Regular(str, Regex.MOBILE.getRegex());
    }

    /**
     * 判断是否为Url 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isUrl(String str) {
        return Regular(str, Regex.URL.getRegex());
    }

    /**
     * 判断字段是否为数字 正负整数 正负浮点数 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNumber(String str) {
        return Regular(str, Regex.DOUBLE.getRegex());
    }

    /**
     * 判断字段是否为INTEGER  符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isInteger(String str) {
        return Regular(str, Regex.INTEGER.getRegex());
    }

    /**
     * 判断字段是否为正整数正则表达式
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isINTEGER_NEGATIVE(String str) {
        return Regular(str, Regex.INTEGER_NEGATIVE.getRegex());
    }

    /**
     * 判断字段是否为负整数正则表达式
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isINTEGER_POSITIVE(String str) {
        return Regular(str, Regex.INTEGER_POSITIVE.getRegex());
    }

    /**
     * 判断字段是否为DOUBLE 符合返回ture
     *
     * @param str
     * @return boolean
     */
    public static boolean isDouble(String str) {
        return Regular(str, Regex.DOUBLE.getRegex());
    }

    /**
     * 判断字段是否为正浮点数正则表达式
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isDOUBLE_NEGATIVE(String str) {
        return Regular(str, Regex.DOUBLE_NEGATIVE.getRegex());
    }

    /**
     * 判断字段是否为负浮点数正则表达式
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isDOUBLE_POSITIVE(String str) {
        return Regular(str, Regex.DOUBLE_POSITIVE.getRegex());
    }

    /**
     * 判断字段是否为日期 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isDate(String str) {
        return Regular(str, Regex.DATE_ALL.getRegex());
    }

    /**
     * 验证2010-12-10
     *
     * @param str 字符串
     * @return
     */
    public static boolean isDate1(String str) {
        return Regular(str, Regex.DATE_FORMAT1.getRegex());
    }

    /**
     * 判断字段是否为年龄 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isAge(String str) {
        return Regular(str, Regex.AGE.getRegex());
    }

    /**
     * 判断字段是否超长
     * 字串为空返回fasle, 超过长度{leng}返回ture 反之返回false
     *
     * @param str 字符串
     * @param leng
     * @return boolean
     */
    public static boolean isLengOut(String str, int leng) {
        return strIsNull(str) ? false : str.trim().length() > leng;
    }

    /**
     * 判断字段是否为身份证 符合返回ture
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isIdCard(String str) {
        if (strIsNull(str)) {
            return false;
        }
        if (str.trim().length() == 15 || str.trim().length() == 18) {
            return Regular(str, Regex.IDCARD.getRegex());
        } else {
            return false;
        }

    }

    /**
     * 判断字段是否为邮编 符合返回ture
     *
     * @param str
     * @return boolean
     */
    public static boolean isCode(String str) {
        return Regular(str, Regex.CODE.getRegex());
    }

    /**
     * 判断字符串是不是全部是英文字母
     *
     * @param str
     * @return boolean
     */
    public static boolean isEnglish(String str) {
        return Regular(str, Regex.STR_ENG.getRegex());
    }

    /**
     * 判断字符串是不是全部是英文字母+数字
     *
     * @param str
     * @return boolean
     */
    public static boolean isENG_NUM(String str) {
        return Regular(str, Regex.STR_ENG_NUM.getRegex());
    }

    /**
     * 判断字符串是不是全部是英文字母+数字+下划线
     *
     * @param str
     * @return boolean
     */
    public static boolean isENG_NUM_(String str) {
        return Regular(str, Regex.STR_ENG_NUM_.getRegex());
    }

    /**
     * 过滤特殊字符串 返回过滤后的字符串
     *
     * @param str
     * @return boolean
     */
    public static String filterStr(String str) {
        Pattern p = Pattern.compile(Regex.STR_SPECIAL.getRegex());
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 校验机构代码格式
     *
     * @return
     */
    public static boolean isJigouCode(String str) {
        return Regular(str, Regex.JIGOU_CODE.getRegex());
    }

    /**
     * 判断字符串是不是数字组成
     *
     * @param str
     * @return boolean
     */
    public static boolean isSTR_NUM(String str) {
        return Regular(str, Regex.STR_NUM.getRegex());
    }

    /**
     * 匹配是否符合正则表达式pattern 匹配返回true
     *
     * @param str     匹配的字符串
     * @param pattern 匹配模式
     * @return boolean
     */
    private static boolean Regular(String str, String pattern) {
        if (null == str || str.trim().length() <= 0) {
            return false;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
