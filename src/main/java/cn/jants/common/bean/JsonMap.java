package cn.jants.common.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class JsonMap extends HashMap {

    public JsonMap() {
    }

    public JsonMap(Object javaObject) {
        if (javaObject != null) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(javaObject);
            putAll(jsonObject);
        }
    }


    public static <K, V> JsonMap newJsonMap() {
        return new JsonMap();
    }

    public <T> T toJavaObject(Class<T> cls) {
        JSON jsonMap = (JSON) JSON.toJSON(this);
        return JSON.toJavaObject(jsonMap, cls);
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public JsonMap set(Object key, Object value) {
        put(key, value);
        return this;
    }

    public String getStr(String key) {
        Object o = super.get(key);
        if (o == null) {
            return null;
        }
        return String.valueOf(o);
    }

    public String getStr(String key, String defaultValue) {
        Object o = super.get(key);
        if (o == null) {
            return defaultValue;
        }
        return String.valueOf(o);
    }

    public Integer getInt(String key) {
        Object o = super.get(key);
        if (o == null || "".equals(o)) {
            return null;
        }
        return Integer.parseInt(getStr(key));
    }

    public Integer getInt(String key, Integer defaultValue) {
        Object o = super.get(key);
        if (o == null || "".equals(o)) {
            return defaultValue;
        }
        return Integer.parseInt(getStr(key));
    }

    public Long getLong(String key) {
        Object o = super.get(key);
        if (o == null || "".equals(o)) {
            return null;
        }
        return Long.parseLong(getStr(key));
    }

    public Long getLong(String key, Long defaultValue) {
        Object o = super.get(key);
        if (o == null || "".equals(o)) {
            return defaultValue;
        }
        return Long.parseLong(getStr(key));
    }

    public Boolean getBoolean(String key) {
        Object o = super.get(key);
        if (o == null || "".equals(o)) {
            return null;
        }
        return Boolean.parseBoolean(getStr(key));
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object o = super.get(key);
        if (o == null || "".equals(o)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(getStr(key));
    }

    public Date getDate(String key, String formatStr) {
        DateFormat df = new SimpleDateFormat(formatStr);
        try {
            return df.parse(getStr(key));
        } catch (ParseException e) {
            Log.error("Date class transition anomaly.");
            return null;
        }
    }

    public BigDecimal getBigDecimal(String key) {
        return new BigDecimal(getStr(key));
    }

    @Override
    public Object put(Object key, Object value) {
        if (value == null) {
            return super.put(key, "");
        }
        return super.put(key, value);
    }
}
