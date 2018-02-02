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

    public JsonMap(){}

    public JsonMap(Object javaObject){
        if(javaObject != null) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(javaObject);
            putAll(jsonObject);
        }
    }


    public static <K, V> JsonMap newJsonMap() {
        return new JsonMap();
    }

    public <T> T toJavaObject(Class<T> cls){
        JSON jsonMap = (JSON) JSON.toJSON(this);
        return JSON.toJavaObject(jsonMap, cls);
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
