package com.ants.core.context;

import com.aliyun.oss.OSSClient;
import com.ants.common.bean.Log;
import com.ants.common.bean.Prop;
import com.ants.common.utils.GenUtil;
import com.ants.common.utils.StrUtil;
import com.ants.plugin.cache.EhCacheTpl;
import com.ants.plugin.cache.RedisTpl;
import com.ants.plugin.jms.ActiveMqTpl;
import com.ants.plugin.oss.AliOssTpl;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017/12/27
 */
public final class AntsTool {

    private static final ConcurrentMap<String, Object> PLUGINS = new ConcurrentHashMap<>();

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
        String key = "redis_".concat(GenUtil.md5Str(host, port, password, database));
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
            throw new RuntimeException("没有在配置文件中找到Redis默认配置");
        }
        return getRedis(host, port, password, database);
    }

    /**
     * 获取EhCache实例
     *
     * @param cls        当前jar包类的class
     * @param configName 配置文件
     * @return
     */
    public static EhCacheTpl getEhCache(Class cls, String configName) {
        String key = "ehcache_".concat(GenUtil.md5Str(configName));
        if (PLUGINS.containsKey(key)) {
            return (EhCacheTpl) PLUGINS.get(key);
        }
        CacheManager cacheManager;
        if (cls != null) {
            InputStream resourceAsStream = cls.getResourceAsStream(configName == null ? "/ehcache.xml" : Prop.getKeyStrValue(configName));
            //创建缓存管理器
            cacheManager = CacheManager.create(resourceAsStream);
        } else {
            cacheManager = new CacheManager();
        }
        Cache cache = new Cache(EhCacheTpl.DEFAULT_CACHE, 5000, true, false, 36000, 36000);
        cacheManager.addCache(cache);
        Log.debug("Ehcache 缓存插件加载成功... ");
        return new EhCacheTpl(cacheManager);
    }

    public static EhCacheTpl getEhCache(Class cls) {
        return getEhCache(cls, null);
    }

    public static EhCacheTpl getEhCache() {
        return getEhCache(null, null);
    }

    /**
     * 获取ActiveMq实例
     *
     * @param brokenUrl 链接地址
     * @param username  账号
     * @param password  密码
     * @return
     */
    public static ActiveMqTpl getActiveMq(String brokenUrl, String username, String password) {
        username = Prop.getKeyStrValue(username);
        password = Prop.getKeyStrValue(password);
        brokenUrl = Prop.getKeyStrValue(brokenUrl);
        String key = "activemq_".concat(GenUtil.md5Str(brokenUrl, username, password));
        if (PLUGINS.containsKey(key)) {
            return (ActiveMqTpl) PLUGINS.get(key);
        }
        try {
            //创建一个链接工厂
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password, brokenUrl);
            //从工厂中创建一个链接
            Connection connection = connectionFactory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            Session session = connection.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);
            Log.debug("ActiveMq Jms 连接成功... ");
            return new ActiveMqTpl(session);
        } catch (JMSException e) {
            throw new RuntimeException(String.format("error , ActiveMq连接失败... -> %s", e.getMessage()));
        }
    }

    public static ActiveMqTpl getActiveMq() {
        String brokenUrl = Prop.getStr("ants.activemq.broken-url");
        String username = Prop.getStr("ants.activemq.username");
        String password = Prop.getStr("ants.activemq.password");
        if (StrUtil.notBlank(brokenUrl, username, password)) {
            throw new RuntimeException("没有在配置文件中找到ActiveMq默认配置");
        }
        return getActiveMq(brokenUrl, username, password);
    }

    /**
     * 获取阿里云Oss实例
     *
     * @param url
     * @param accessKeyId
     * @param accessKeySecret
     * @return
     */
    public static AliOssTpl getAliOss(String url, String accessKeyId, String accessKeySecret) {
        url = Prop.getKeyStrValue(url);
        accessKeyId = Prop.getKeyStrValue(accessKeyId);
        accessKeySecret = Prop.getKeyStrValue(accessKeySecret);
        String endpoint, bucketName;
        try {
            endpoint = url.substring(url.indexOf(".") + 1, url.length());
            bucketName = url.substring(url.indexOf("//") + 2, url.indexOf("."));
            Log.debug("endpoint:{} , bucketName:{}", endpoint, bucketName);
        } catch (Exception e) {
            throw new RuntimeException(String.format("AliOss配置错误 -> %s", e.getMessage()));
        }
        String key = "oss_".concat(GenUtil.md5Str(url, accessKeyId, accessKeySecret));
        if (PLUGINS.containsKey(key)) {
            return (AliOssTpl) PLUGINS.get(key);
        }
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        return new AliOssTpl(client, bucketName, endpoint);
    }

    public static AliOssTpl getAliOss() {
        String url = Prop.getStr("ants.alioss.url");
        String accessKeyId = Prop.getStr("ants.alioss.access-key-id");
        String accessKeySecret = Prop.getStr("ants.alioss.access-key-secret");
        if (StrUtil.notBlank(url, accessKeyId, accessKeySecret)) {
            throw new RuntimeException("没有在配置文件中找到ActiveMq默认配置");
        }
        return getAliOss(url, accessKeyId, accessKeySecret);
    }
}
