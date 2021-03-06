package cn.jants.common.utils;

import cn.jants.common.enums.EncType;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * 加密工具类, 带MD5/DES/3DES/AES等加密方式
 *
 * @author MrShun
 * @version 1.0
 */
public class StrEncryptUtil {

    private static final Logger LOG = LoggerFactory.getLogger(StrEncryptUtil.class);

    private static final String UTF_8 = "UTF-8";

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 共DES、3DES、AES等加密方式
     *
     * @param password 密码
     * @param type     加密方式
     * @param str      需要加密字符串
     * @return
     */
    public static String encrypt(String password, EncType type, String str) {
        try {
            if(type == EncType.AES && password.length() != 16){
                LOG.error("AES 加密密钥必须是16位！");
                return null;
            }
            String encType = String.valueOf(type);
            int len = keyLen(type);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(encType);
            //初始化秘钥
            keyGenerator.init(len, new SecureRandom(password.getBytes(UTF_8)));
            //获取密钥
            SecretKey secretKey = new SecretKeySpec(password.getBytes(), encType);
            //Cipher完成加密或解密工作类
            //AES默认是AES/ECB/PKCS5Padding模式
            Cipher cipher = Cipher.getInstance(encType);
            //对Cipher初始化，加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherByte = cipher.doFinal(str.getBytes(UTF_8));
            return Base64.encodeBase64String(cipherByte);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("{} 加密异常, 加密字符 > {}", type, str);
            return null;
        }

    }

    /**
     * 共DES、3DES、AES等解密方式
     *
     * @param password 密码
     * @param type     解密方式
     * @param str      需要解密字符串
     * @return
     */
    public static String decrypt(String password, EncType type, String str) {
        try {
            String encType = String.valueOf(type);
            byte[] data = Base64.decodeBase64(str.getBytes(UTF_8));
            int len = keyLen(type);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(encType);
            //初始化秘钥
            keyGenerator.init(len, new SecureRandom(password.getBytes(UTF_8)));
            //获取密钥
            SecretKey secretKey = new SecretKeySpec(password.getBytes(), encType);
            //Cipher完成加密或解密工作类
            Cipher cipher = Cipher.getInstance(encType);
            //对Cipher初始化，解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(data);
            return new String(bytes, UTF_8);
        } catch (Exception e) {
            LOG.info("{} 解密失败, 解密字符 > {}", type, str);
            return null;
        }

    }

    /**
     * 根据不同的加密方式获取秘钥长度
     *
     * @param encType 加密方式
     * @return
     */
    private static int keyLen(EncType encType) {
        if (encType == EncType.DES) {
            return 56;
        } else if (encType == EncType.DESede) {
            return 168;
        } else if (encType == EncType.AES) {
            return 128;
        }
        return 0;
    }
}
