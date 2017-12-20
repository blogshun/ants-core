package com.ants.core.utils;

import com.ants.common.bean.Log;
import com.ants.common.utils.StrUtil;
import com.ants.core.context.AppConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-05-05
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
            if (inputStream == null && fileName != AppConstant.DEFAULT_CONFIG) {
                throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
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
        return Integer.parseInt(String.valueOf(PROP.get(key)));
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



