package cn.jants.plugin.cache;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author MrShun
 * @version 1.0
 */
public class RedisTpl {

    private JedisPool jedisPool;

    public Jedis getJedis() {
        return jedisPool.getResource();

    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public RedisTpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }


    /**
     * 永久写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, String value) {
        boolean result = false;
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            result = true;
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
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
    public boolean set(String key, String value, int seconds) {
        boolean result = false;
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            jedis.expire(key, seconds);
            result = true;
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 通过key获取value值的长度
     *
     * @param key
     * @return
     */
    public Long strLen(String key) {
        Jedis jedis = getJedis();
        Long strLen = null;
        try {
            strLen = jedis.strlen(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return strLen;
    }

    /**
     * 设置key value,如果key已经存在则返回0
     *
     * @param key
     * @param value
     * @return
     */
    public Long setNx(String key, String value) {
        Jedis jedis = getJedis();
        Long setNx = null;
        try {
            setNx = jedis.setnx(key, value);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return setNx;
    }

    /**
     * 设置key的超时时间为seconds
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(String key, int seconds) {
        Jedis jedis = getJedis();
        Long expire = null;
        try {
            expire = jedis.expire(key, seconds);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return expire;
    }

    /**
     * 根据前缀查询所有值
     *
     * @param pattern
     * @return
     */
    public List<String> getByPattern(String pattern) {
        Jedis jedis = getJedis();
        Set<String> keys = jedis.keys(pattern);
        List<String> ret = new ArrayList<>();
        try {
            if (keys != null && !keys.isEmpty()) {
                String[] keyArr = new String[keys.size()];
                ret = jedis.mget(keyArr);
            }
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return ret;
    }


    /**
     * 删除对应的value
     *
     * @param keys
     */
    public void remove(String... keys) {
        Jedis jedis = getJedis();
        try {
            for (String key : keys) {
                if (jedis.exists(key)) {
                    jedis.del(key);
                }
            }
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据前缀删除所有值
     *
     * @param pattern
     */
    public void removeByPattern(String pattern) {
        Jedis jedis = getJedis();
        Set<String> keys = jedis.keys(pattern);
        try {
            if (keys != null && !keys.isEmpty()) {
                String[] keyArr = new String[keys.size()];
                jedis.del(keys.toArray(keyArr));
            }
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    /**
     * 判断键值是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(String key) {
        Jedis jedis = getJedis();
        Boolean exists = null;
        try {
            exists = jedis.exists(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return exists;
    }

    /**
     * 读取缓存, 获取字符串对象
     *
     * @param key
     * @return
     */
    public String get(String key) {
        Jedis jedis = getJedis();
        String jsonStr = null;
        try {
            jsonStr = jedis.get(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return jsonStr;
    }

    /**
     * 读取缓存, 存储对象值
     *
     * @param key
     * @return
     */
    public <T> T get(String key, Class<T> cls) {
        Jedis jedis = getJedis();
        T obj = null;
        try {
            String jsonStr = jedis.get(key);
            obj = JSON.parseObject(jsonStr, cls);
        }catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return obj;
    }
}
