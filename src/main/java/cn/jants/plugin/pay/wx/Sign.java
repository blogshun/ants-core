package cn.jants.plugin.pay.wx;

import cn.jants.common.bean.Log;
import cn.jants.common.utils.StrEncryptUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Sign签名工具
 *
 * @author MrShun
 * @version 1.0
 */
public class Sign {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 采用TreeMap 按首字母排序 统一小写字母 生成MD5签名
     *
     * @param map    对象数据
     * @param payKey 支付秘钥
     * @return
     */
    public static String md5Sign(TreeMap map, String payKey) {
        String stringSignTemp = pj(map);
        if (payKey != null) {
            stringSignTemp = stringSignTemp + "&key=" + payKey;
        }
        Log.debug("签名Str > {}", stringSignTemp);
        return StrEncryptUtil.md5(stringSignTemp).toUpperCase();
    }

    /**
     * sha1签名
     *
     * @param str 签名字符串
     * @return
     */
    public static String sha1(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            byte[] bytes = messageDigest.digest();
            int len = bytes.length;
            StringBuilder buf = new StringBuilder(len * 2);
            // 把密文转换成十六进制的字符串形式  
            for (int j = 0; j < len; j++) {
                buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
                buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
            }
            return buf.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 符号拼接
     *
     * @return
     */
    public static String pj(TreeMap map) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append(key + "=" + value + "&");
        }
        String stringSignTemp = String.valueOf(sb);
        return stringSignTemp.substring(0, stringSignTemp.length() - 1);
    }


    public static String hmacSha256Sign(TreeMap map, String payKey) {
        String stringSignTemp = pj(map);
        if (payKey != null) {
            stringSignTemp = stringSignTemp + "&key=" + payKey;
        }
        Log.debug("签名Str > {}", stringSignTemp);
        return sha256HMAC(stringSignTemp, payKey).toUpperCase();
    }

    /**
     * sha256_HMAC加密
     *
     * @param data 加密数据
     * @param key  秘钥
     * @return 加密后字符串
     */
    private static String sha256HMAC(String data, String key) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] array = sha256HMAC.doFinal(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }
}
