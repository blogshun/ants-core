package com.ants.plugin.cache;

import com.ants.common.bean.Log;
import com.ants.common.utils.StrUtil;
import com.ants.core.ext.Plugin;
import com.ants.core.module.ServiceManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import java.io.InputStream;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-12-05
 */
public class EhCachePlugin implements Plugin {


    /**
     * 配置文件
     */
    private String fileName = "/ehcache.xml";


    public EhCachePlugin(String fileName) {
        if (StrUtil.notBlank(fileName)) {
            this.fileName = StrUtil.setFirstInitial(fileName, '/');
        }
    }

    @Override
    public boolean start() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(fileName);
        //创建缓存管理器
        CacheManager cacheManager = CacheManager.create(resourceAsStream);
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
        return false;
    }
}
