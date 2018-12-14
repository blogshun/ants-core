package cn.jants.common.bean;

import cn.jants.common.exception.TipException;
import cn.jants.common.utils.StrUtil;

import java.util.regex.Pattern;

/**
 * 数据校验工具
 * 1. 电话号码 2. 邮编 3. QQ 4. E-mail 5. 手机号码 6. URL 7. 是否为数字 8. 是否为中文 9. 身份证 10. 域名 11. IP 。。。。
 *
 * @author MrShun
 * @version 1.0
 */
public class TipValidate {

    /**
     * 值为Null时抛出异常
     *
     * @param val
     * @param msg
     */
    public static void isNull(String msg, Object... val) {
        if (!StrUtil.notNull(val)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 不符合正则时抛出异常
     *
     * @param pattern 正则表达式
     * @param msg     提示消息
     */
    public static void regex(String pattern, String msg, String... val) {
        for (String s : val) {
            if (!Pattern.matches(pattern, s)) {
                throw new TipException(5002, msg);
            }
        }
    }

    /**
     * 值为Null时抛出异常
     *
     * @param val
     * @param msg
     */
    public static void isBlank(String msg, String... val) {
        if (!StrUtil.notBlank(val)) {
            throw new TipException(5002, msg);
        }
    }


    /**
     * 验证Email
     *
     * @param msg
     * @param email email地址，格式：zhangsan@zuidaima.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     */
    public static void isEmail(String msg, String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        if (!Pattern.matches(regex, email)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证身份证号码
     *
     * @param msg
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     */
    public static void isIdCard(String msg, String idCard) {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        if (!Pattern.matches(regex, idCard)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param msg
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *               、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *               <p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
     *               <p>电信的号段：133、153、180（未启用）、189</p>
     */
    public static void isMobile(String msg, String mobile) {
        String regex = "(\\+\\d+)?1[34578]\\d{9}$";
        if (!Pattern.matches(regex, mobile)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证固定电话号码
     *
     * @param msg
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     *              <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     *              数字之后是空格分隔的国家（地区）代码。</p>
     *              <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     *              对不使用地区或城市代码的国家（地区），则省略该组件。</p>
     *              <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
     */
    public static void isPhone(String msg, String phone) {
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        if (!Pattern.matches(regex, phone)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证整数（正整数和负整数）
     *
     * @param msg
     * @param digit 一位或多位0-9之间的整数
     */
    public static void isDigit(String msg, String digit) {
        String regex = "\\-?[1-9]\\d+";
        if (!Pattern.matches(regex, digit)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param msg
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     */
    public static void isDecimals(String msg, String decimals) {
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
        if (!Pattern.matches(regex, decimals)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证中文
     *
     * @param msg
     * @param chinese 中文字符
     */
    public static void isChinese(String msg, String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        if (!Pattern.matches(regex, chinese)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证日期（年月日）
     *
     * @param msg
     * @param date 日期，格式：1992-09-03，或1992.09.03
     */
    public static void isDate(String msg, String date) {
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        if (!Pattern.matches(regex, date)) {
            throw new TipException(5002, msg);
        }
    }

    /**
     * 验证URL地址
     *
     * @param msg
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     */
    public static void isURL(String msg, String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        if (!Pattern.matches(regex, url)) {
            throw new TipException(5002, msg);
        }
    }


    /**
     * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     *
     * @param msg
     * @param ipAddress IPv4标准地址
     */
    public static void isIpAddress(String msg, String ipAddress) {
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        if (!Pattern.matches(regex, ipAddress)) {
            throw new TipException(5002, msg);
        }
    }

}
