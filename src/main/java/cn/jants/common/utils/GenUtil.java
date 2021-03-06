package cn.jants.common.utils;

import cn.jants.common.enums.EncType;

import java.util.UUID;

/**
 * 生成字符或者key工具类
 *
 * @author MrShun
 * @version 1.0
 */
public class GenUtil {

    public static final String SERIAL_PATTERN_NUMBER = "%14s%3s%015d";

    private static final String PATTERN_DATE = "yyyyMMddHHmmss";

    /**
     * 生成UUID
     */
    public static String uuid() {
        return String.valueOf(UUID.randomUUID());
    }

    /**
     * 生成指定格式的唯一序列号[14位时间戳，年月日时分秒，各两位][3位毫秒数][15顺序数]
     *
     * @return 字符串
     */
    public static String formatSerial() {
        String millis = String.valueOf(System.currentTimeMillis());
        millis = millis.substring((millis.length() - 3), millis.length());
        return String.format(SERIAL_PATTERN_NUMBER, DateUtil.getDataTime(PATTERN_DATE), millis, System.currentTimeMillis());
    }

    /**
     * 根据多个参数生成md5密钥
     *
     * @param params 对象数组
     * @return 密钥字符串
     */
    public static String makeMd5Str(Object... params) {
        StringBuffer sb = new StringBuffer();
        for (Object param : params) {
            if (StrUtil.notNull(param)) {
                sb.append(param);
            }
        }
        return StrEncryptUtil.md5(sb.toString());
    }

    /**
     * 生成token
     *
     * @param params 对象数组
     * @return token字符串
     */
    public static String makeTokenStr(Object... params) {
        StringBuffer sb = new StringBuffer();
        for (Object param : params) {
            if (StrUtil.notNull(param)) {
                sb.append(param);
            }
        }
        return StrEncryptUtil.encrypt("_user_token_key_", EncType.AES, sb.toString());
    }
}
