package cn.jants.plugin.cache;

import cn.jants.common.bean.Log;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.ext.Plugin;
import cn.jants.core.module.ServiceManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author MrShun
 * @version 1.0
 */
public class RedisPlugin implements Plugin {

    /**
     * 主机地址, 库名称, 密码
     */
    private String host, password;
    /**
     * 端口号
     */
    private int port, database;

    private RedisTpl redisTpl;

    public RedisPlugin(String host, int port, Integer database, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.password = password;
    }

    @Override
    public boolean start() throws Exception {

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
            jedisPool = new JedisPool(config, host, port, 100000, password, database);
        } else {
            jedisPool = new JedisPool(config, host, port, 100000);
        }
        try {
            Jedis jedis = jedisPool.getResource();
            jedis.select(database);
            Log.debug("db > db{} , Redis连接成功... ", database);
            jedis.close();
            redisTpl = new RedisTpl(jedisPool);
            ServiceManager.setService("plugin_cache_RedisTpl", redisTpl);
            return true;
        } catch (Exception e) {
            Log.error("Redis连接失败, 请认真检查配置 ... ", e.getMessage());
            throw new Exception(e);
        }

    }

    public RedisTpl getRedisTpl() {
        return redisTpl;
    }

    @Override
    public boolean destroy() {
        return true;
    }
}
