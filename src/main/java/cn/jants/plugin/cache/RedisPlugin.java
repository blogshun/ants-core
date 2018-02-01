package cn.jants.plugin.cache;

import cn.jants.common.bean.Log;
import cn.jants.core.ext.Plugin;
import cn.jants.core.module.ServiceManager;
import cn.jants.common.utils.StrUtil;
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

    private Integer maxTotal = 500, maxIdle = 1000 * 60, maxWaitMillis = 1000 * 10;

    public RedisPlugin(String host, int port, Integer database, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.password = password;
    }

    @Override
    public boolean start() throws Exception {
        try {
            // 1.初始化
            // 连接本地的 Redis 服务
            Jedis jedis = new Jedis(host, port);
            JedisPoolConfig config = new JedisPoolConfig();
            //最大活动的对象个数
            config.setMaxTotal(maxTotal);
            //对象最大空闲时间
            config.setMaxIdle(maxIdle);
            //获取对象时最大等待时间
            config.setMaxWaitMillis(maxWaitMillis);
            config.setTestOnBorrow(true);
            jedis.setDataSource(new JedisPool(config, host, port));
            if(StrUtil.notBlank(password)) {
                jedis.auth(password);
            }
            jedis.select(database);
            Log.debug("db > db{} , Redis连接成功... ", database);

            //初始化RedisTpl
            RedisTpl redisTpl = new RedisTpl(jedis);
            ServiceManager.setService("plugin_cache_RedisTpl", redisTpl);
            return true;
        } catch (Exception e) {
            Log.error("Redis连接失败, 请认真检查配置 ... ", e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public boolean destroy() {
        return true;
    }
}
