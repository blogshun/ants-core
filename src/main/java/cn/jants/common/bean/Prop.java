package cn.jants.common.bean;

import cn.jants.common.utils.StrUtil;
import cn.jants.core.utils.PropertyUtil;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MrShun
 * @version 1.0
 *          Date 2017-11-24
 */
public class Prop extends PropertyUtil {

    private static final String START_SYMBOL = "${", END_SYMBOL = "}";

    /**
     * 根据内存中配置文件键获取值
     *
     * @param clsName 指定类
     * @param key     键
     * @return 对象值
     */
    public static Object getKeyValue(String clsName, String key) {
        if (key.startsWith(START_SYMBOL) && key.endsWith(END_SYMBOL)) {
            String value = key.replace(START_SYMBOL, "").replace(END_SYMBOL, "");
            Object paramValue = null;
            int i = value.indexOf(":");
            if (i != -1) {
                paramValue = value.substring(i + 1, value.length());
            } else {
                paramValue = get(value);
                if (paramValue == null && clsName != null) {
                    throw new RuntimeException(clsName + " 中 " + key + " 没有在配置文件中找到属性, 却注入了属性!");
                }
            }
            return paramValue;
        } else {
            return key;
        }
    }

    public static String getKeyStrValue(String key) {
        return String.valueOf(getKeyValue(null, key));
    }

    public static Integer getKeyIntValue(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        return Integer.parseInt(getKeyStrValue(key));
    }

    /**
     * 查询前缀能够匹配到的属性
     *
     * @param prefix 属性前缀
     * @return 配置对象
     */
    public static Properties getProperties(String prefix) {
        ConcurrentHashMap prop = getProp();
        Properties result = new Properties();
        Set<Map.Entry<String, Object>> sets = prop.entrySet();
        for (Map.Entry<String, Object> entry : sets) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(prefix)) {
                String keyStr = key.replace(prefix.concat("."), "");
                result.put(keyStr, value);
            }
        }
        return result;
    }
}
