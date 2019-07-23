package cn.jants.common.bean;

import cn.jants.common.utils.StrUtil;
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
            JsonMap jsonMap = JSON.parseObject(JSON.toJSONString(javaObject), getClass());
            putAll(jsonMap);
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
        String val = getStr(key);
        if(StrUtil.isBlank(val)){
            return new BigDecimal(0);
        }
        return new BigDecimal(val);
    }

    public JsonMap getJsonMap(String key) {
        return JSON.parseObject(getStr(key), JsonMap.class);
    }

    public String toJsonString(boolean isFormat) {
        return JSON.toJSONString(this, isFormat);
    }
}
