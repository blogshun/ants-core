package cn.jants.plugin.tool;

import cn.jants.common.bean.Log;
import cn.jants.common.bean.Prop;
import cn.jants.common.utils.GenUtil;
import cn.jants.plugin.cache.EhCacheTpl;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import java.io.InputStream;

/**
 * @author MrShun
 * @version 1.0
 */
public class EhCacheTool extends ConcurrentToolMap {

    /**
     * 获取EhCache实例
     *
     * @param cls        当前jar包类的class
     * @param configName 配置文件
     * @return
     */
    public static EhCacheTpl getEhCache(Class cls, String configName) {
        String key = "ehcache_".concat(GenUtil.makeMd5Str(configName));
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
}
