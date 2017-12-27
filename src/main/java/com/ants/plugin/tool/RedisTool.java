package com.ants.plugin.tool;

import com.ants.common.bean.Log;
import com.ants.common.bean.Prop;
import com.ants.common.utils.GenUtil;
import com.ants.common.utils.StrUtil;
import com.ants.plugin.cache.RedisTpl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-27
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
        }
        // 1.初始化
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis(host, Integer.valueOf(port));
        JedisPoolConfig config = new JedisPoolConfig();
        //最大活动的对象个数
        config.setMaxTotal(500);
        //对象最大空闲时间
        config.setMaxIdle(1000 * 60);
        //获取对象时最大等待时间
        config.setMaxWaitMillis(1000 * 10);
        config.setTestOnBorrow(true);
        jedis.setDataSource(new JedisPool(config, host, Integer.valueOf(port)));
        if (StrUtil.notBlank(password)) {
            jedis.auth(password);
        }
        try {
            if (StrUtil.notBlank(database)) {
                jedis.select(Integer.valueOf(database));
            } else {
                jedis.select(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("error , Redis连接失败... -> %s", e.getMessage()));
        }
        Log.debug("db > db{} , Redis连接成功... ", database);
        return new RedisTpl(jedis);
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
