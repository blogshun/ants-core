package cn.jants.common.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author MrShun
 * @version 1.0
 */
public class RandomUtil {

    private static final String PATTERN_DATE = "yyyyMMddHHmmss";

    private static final String EN_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 生成UUID
     */
    public static String uuId() {
        return String.valueOf(UUID.randomUUID());
    }

    /**
     * 随机生成 19位随机数
     *
     * @param prefixStr
     * @return
     */
    public static String orderId(String prefixStr) {
        String millis = String.valueOf(System.currentTimeMillis());
        millis = millis.substring((millis.length() - 5), millis.length());
        return prefixStr.concat(DateUtil.getDataTime(PATTERN_DATE)).concat(millis);
    }

    public static String orderId() {
        return orderId("");
    }


    /**
     * 生成随机英文字符串, 默认10位
     *
     * @param num 位数
     * @return
     */
    public static String enStr(Integer num) {
        char[] chars = EN_STR.toCharArray();
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int n = random.nextInt(chars.length);
            sb.append(chars[n]);
        }
        return sb.toString();
    }

    public static String enStr() {
        return enStr(10);
    }
}
