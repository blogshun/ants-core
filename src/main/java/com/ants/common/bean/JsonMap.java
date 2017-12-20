package com.ants.common.bean;

import java.util.HashMap;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-05-17
 */
public class JsonMap extends HashMap {

    private JsonMap() {
    }

    public static <K, V> JsonMap newJsonMap() {
        return new JsonMap();
    }

    public JsonMap set(Object key, Object value) {
        put(key, value);
        return this;
    }

    public String getStr(String key) {
        String val = String.valueOf(get(key));
        if (val == null || "".equals(val)) {
            return val;
        }
        return val.trim();
    }

    public String getStr(String key, String defaultValue) {
        return get(key) == null ? defaultValue : String.valueOf(get(key));
    }

    public Integer getInt(String key) {
        return Integer.parseInt(String.valueOf(get(key)));
    }

    public Integer getInt(String key, Integer defaultValue) {
        return get(key) == null ? defaultValue : Integer.parseInt(String.valueOf(get(key)));
    }

    public Long getLong(String key) {
        return Long.parseLong(String.valueOf(get(key)));
    }

    public Long getLong(String key, Long defaultValue) {
        return get(key) == null ? defaultValue : Long.parseLong(String.valueOf(get(key)));
    }

    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(String.valueOf(get(key)));
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return get(key) == null ? defaultValue : Boolean.parseBoolean(String.valueOf(get(key)));
    }

    @Override
    public Object put(Object key, Object value) {
        if (value == null) {
            return super.put(key, "");
        }
        return super.put(key, value);
    }
}
