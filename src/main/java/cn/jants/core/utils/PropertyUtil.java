package cn.jants.core.utils;

import cn.jants.common.bean.Log;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.context.AppConstant;
import cn.jants.common.enums.EncType;
import cn.jants.common.utils.StrEncryptUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class PropertyUtil {

    private static ConcurrentHashMap PROP = null;

    public PropertyUtil() {
    }

    /**
     * 需要加载的配置
     *
     * @param fileNames 配置文件列表
     */
    public static void use(String... fileNames) {
        for (String fileName : fileNames) {
            if (StrUtil.notBlank(fileName)) {
                load(fileName, AppConstant.DEFAULT_ENCODING);
            }
        }
    }


    /**
     * 读取文件
     *
     * @param fileName 资源配置名
     * @param encoding 编码集
     */
    private static void load(String fileName, String encoding) {
        if (PROP == null) {
            PROP = new ConcurrentHashMap(20);
        }
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
            if(StrUtil.notBlank(AppConstant.SECRET_KEY)) {
                Set<Map.Entry<Object, Object>> entries = properties.entrySet();
                Map secretMap = new HashMap();
                for (Map.Entry<Object, Object> entry : entries) {
                    String key = String.valueOf(entry.getKey());
                    if (key.startsWith("@")) {
                        String value = String.valueOf(entry.getValue());
                        Log.info("检测到加密属性 > {}", key);
                        value = StrEncryptUtil.decrypt(AppConstant.SECRET_KEY, EncType.AES, value);
                        if(value == null){
                            throw new RuntimeException("配置文件解密失败, 请认真检查配置!");
                        }
                        secretMap.put(key.substring(1), value);
                    }
                }
                properties.putAll(secretMap);
            }
            PROP.putAll(properties);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.error(e.getMessage());
                }
            }
        }
    }

    public static void clear() {
        PROP.clear();
        PROP = null;
    }

    public static ConcurrentHashMap getProp() {
        if (PROP == null) {
            throw new IllegalStateException("Load properties file by invoking PropertyUtil.use(String fileName) method first.");
        }
        return PROP;
    }


    public static Object get(Object key) {
        return PROP.get(key);
    }

    public static Object get(Object key, Object defaultValue) {
        return PROP.get(key) == null ? defaultValue : PROP.get(key);
    }

    public static String getStr(String key) {
        return String.valueOf(PROP.get(key));
    }

    public static String getStr(String key, String defaultValue) {
        return PROP.get(key) == null ? defaultValue : String.valueOf(PROP.get(key));
    }

    public static Integer getInt(String key) {
        Object value = PROP.get(key);
        if(value == null){
            throw new IllegalArgumentException("not found key: " + key);
        }
        return Integer.parseInt(String.valueOf(value));
    }

    public static Integer getInt(String key, Integer defaultValue) {
        return PROP.get(key) == null ? defaultValue : Integer.parseInt(String.valueOf(PROP.get(key)));
    }

    public static Long getLong(String key) {
        return Long.parseLong(String.valueOf(PROP.get(key)));
    }

    public static Long getLong(String key, Long defaultValue) {
        return PROP.get(key) == null ? defaultValue : Long.parseLong(String.valueOf(PROP.get(key)));
    }

    public static Boolean getBoolean(String key) {
        return Boolean.parseBoolean(String.valueOf(PROP.get(key)));
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return PROP.get(key) == null ? defaultValue : Boolean.parseBoolean(String.valueOf(PROP.get(key)));
    }

    public static boolean containsKey(String key) {
        return PROP.containsKey(key);
    }
}



