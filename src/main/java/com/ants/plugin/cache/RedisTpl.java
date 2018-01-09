package com.ants.plugin.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ants.core.utils.ParamTypeUtil;
import redis.clients.jedis.Jedis;

/**
 * @author MrShun
 * @version 1.0
 */
public class RedisTpl {

    private Jedis jedis;

    public RedisTpl(Jedis jedis) {
        this.jedis = jedis;
    }

    public Jedis getJedis() {
        return jedis;
    }

    /**
     * 永久写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            if (ParamTypeUtil.isBaseDataType(value.getClass())) {
                jedis.set(key, String.valueOf(value));
            } else {
                jedis.set(key, JSON.toJSONString(value));
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, int seconds) {
        boolean result = false;
        try {
            set(key, value);
            jedis.expire(key, seconds);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            if (exists(key)) {
                jedis.del(key);
            }
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return jedis.exists(key);
    }

    /**
     * 读取缓存, 存储对象值
     *
     * @param key
     * @return
     */
    public String getStr(final String key) {
        return jedis.get(key);
    }

    /**
     * 读取缓存, 存储对象值
     *
     * @param key
     * @return
     */
    public JSONObject get(final String key) {
        String jsonStr = jedis.get(key);
        return JSON.parseObject(jsonStr);
    }

    /**
     * 读取缓存, 存储对象值
     *
     * @param key
     * @return
     */
    public <T> T get(final String key, Class<T> cls) {
        String jsonStr = jedis.get(key);
        return JSON.parseObject(jsonStr, cls);
    }
}
