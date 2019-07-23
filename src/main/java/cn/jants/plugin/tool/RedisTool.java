package cn.jants.plugin.tool;

import cn.jants.common.bean.Log;
import cn.jants.common.bean.Prop;
import cn.jants.common.utils.GenUtil;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.module.ServiceManager;
import cn.jants.plugin.cache.RedisTpl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author MrShun
 * @version 1.0
 */
public final class RedisTool extends ConcurrentToolMap {


    /**
     * 获取redis实例
     *
     * @param host     主机地址
     * @param port     端口号
     * @param password 密码
     * @param database 数据库
     * @return
     */
    public static RedisTpl getRedis(String host, String port, String password, String database) {
        host = Prop.getKeyStrValue(host);
        port = Prop.getKeyStrValue(port);
        password = Prop.getKeyStrValue(password);
        database = Prop.getKeyStrValue(database);
        String key = "redis_".concat(GenUtil.makeMd5Str(host, port, password, database));
        if (PLUGINS.containsKey(key)) {
            return (RedisTpl) PLUGINS.get(key);
        } else {
            JedisPool jedisPool = initJedisPool(host, port, password);
            try {
                Jedis jedis = jedisPool.getResource();
                jedis.select(Integer.valueOf(database));
                Log.debug("db > db{} , Redis连接成功... ", database);
                jedis.close();
                RedisTpl redisTpl = new RedisTpl(jedisPool);
                PLUGINS.put(key, redisTpl);
                return redisTpl;
            } catch (Exception e) {
                throw new RuntimeException(String.format("error , Redis连接失败... -> %s", e.getMessage()));
            }
        }
    }

    /**
     * 初始化Redis池
     *
     * @param host
     * @param port
     * @param password
     * @return
     */
    private static JedisPool initJedisPool(String host, String port, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大活动的对象个数
        config.setMaxTotal(500);
        //对象最大空闲时间
        config.setMaxIdle(1000 * 60);
        //获取对象时最大等待时间
        config.setMaxWaitMillis(1000 * 10);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        JedisPool jedisPool = null;
        if (StrUtil.notBlank(password)) {
            jedisPool = new JedisPool(config, host, Integer.valueOf(port), 100000, password, 0);
        } else {
            jedisPool = new JedisPool(config, host, Integer.valueOf(port), 100000);
        }
        return jedisPool;
    }

    public static RedisTpl getRedis(String host, String port, String password) {
        return getRedis(host, port, password, null);
    }

    public static RedisTpl getRedis() {
        String host = Prop.getStr("ants.redis.host");
        String port = Prop.getStr("ants.redis.port");
        String password = Prop.getStr("ants.redis.password");
        String database = Prop.getStr("ants.redis.database");
        if (StrUtil.notBlank(host, port)) {
            return getRedis(host, port, password, database);
        }
        throw new RuntimeException("没有在配置文件中找到Redis默认配置");
    }
}
