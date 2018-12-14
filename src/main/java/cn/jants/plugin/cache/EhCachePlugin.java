package cn.jants.plugin.cache;

import cn.jants.common.bean.Log;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.ext.Plugin;
import cn.jants.core.module.ServiceManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import java.io.InputStream;

/**
 * @author MrShun
 * @version 1.0
 */
public class EhCachePlugin implements Plugin {


    /**
     * 配置文件
     */
    private String fileName = "/ehcache.xml";

    private CacheManager cacheManager;

    public EhCachePlugin(String fileName) {
        if (StrUtil.notBlank(fileName)) {
            this.fileName = StrUtil.setFirstInitial(fileName, '/');
        }
    }

    @Override
    public boolean start() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(fileName);
        if (resourceAsStream == null) {
            throw new Exception(String.format("使用EhCachePlugin 必须配置 %s 文件!", fileName));
        }
        //创建缓存管理器
        cacheManager = CacheManager.create(resourceAsStream);
        Cache cache = new Cache(EhCacheTpl.DEFAULT_CACHE, 5000, true, false, 36000, 36000);
        cacheManager.addCache(cache);
        Log.debug("Ehcache 缓存插件加载成功... ");
        //初始化EhCacheTpl
        EhCacheTpl chCacheTpl = new EhCacheTpl(cacheManager);
        ServiceManager.setService("plugin_cache_EhCacheTpl", chCacheTpl);
        return true;
    }

    @Override
    public boolean destroy() {
        cacheManager.shutdown();
        return true;
    }
}
