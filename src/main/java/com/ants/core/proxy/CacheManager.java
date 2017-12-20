package com.ants.core.proxy;

import com.alibaba.fastjson.JSON;
import com.ants.common.bean.Log;
import com.ants.common.utils.StrEncryptUtil;
import com.ants.common.utils.StrUtil;
import com.ants.core.module.ServiceManager;
import com.ants.plugin.cache.CacheEvict;
import com.ants.plugin.cache.CachePut;
import com.ants.plugin.cache.Cacheable;
import com.ants.plugin.cache.EhCacheTpl;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.lang.reflect.Method;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-06-07
 */
public class CacheManager {

    private boolean isOpen = false;

    /**
     * true/存在 false/不存在
     */
    private boolean exist = false;

    /**
     * 执行结果
     */
    private Object result;

    private Cache ehcache;

    private String key;

    private int seconds;

    public CacheManager(Object target, Method method, Object[] args) {
        //存入缓存
        Cacheable cache = method.getDeclaredAnnotation(Cacheable.class);
        if (cache != null) {
            isOpen = true;
            key = StrUtil.notBlank(cache.key()) ? cache.key() : encodeStr(target, method, args);
            EhCacheTpl ehCacheTpl = ServiceManager.getService(EhCacheTpl.class);
            if (ehCacheTpl == null) {
                throw new RuntimeException("获取实例 EhCacheTpl 失败, 检查Ehcache配置!");
            }
            ehcache = StrUtil.isBlank(cache.value()) ? ehCacheTpl.getCache() : ehCacheTpl.getCache(cache.value());
            Element element = ehcache.get(key);
            //缓存不存在, 或者已经过期
            if (element != null && !ehcache.isExpired(element)) {
                exist = true;
                Log.debug("ehcache cache key > {}", key);
                result = ehcache.get(key).getObjectValue();
            }
            seconds = cache.seconds();
            return;
        }

        //清除缓存
        CacheEvict cacheEvict = method.getDeclaredAnnotation(CacheEvict.class);
        if (cacheEvict != null) {
            EhCacheTpl ehCacheTpl = ServiceManager.getService(EhCacheTpl.class);
            if (ehCacheTpl == null) {
                throw new RuntimeException("获取实例 EhCacheTpl 失败, 检查Ehcache配置!");
            }
            ehcache = StrUtil.isBlank(cacheEvict.value()) ? ehCacheTpl.getCache() : ehCacheTpl.getCache(cacheEvict.value());
            if(StrUtil.notBlank(cacheEvict.key())){
                ehcache.remove(key);
            }else{
                ehcache.removeAll();
            }
            return;
        }

        //清空并重新存入缓存
        CachePut cachePut = method.getDeclaredAnnotation(CachePut.class);
        if (cachePut != null) {
            EhCacheTpl ehCacheTpl = ServiceManager.getService(EhCacheTpl.class);
            if (ehCacheTpl == null) {
                throw new RuntimeException("获取实例 EhCacheTpl 失败, 检查Ehcache配置!");
            }
            ehcache = StrUtil.isBlank(cachePut.value()) ? ehCacheTpl.getCache() : ehCacheTpl.getCache(cachePut.value());
            if(StrUtil.notBlank(cachePut.key())){
                ehcache.remove(key);
            }else{
                ehcache.removeAll();
            }
            seconds = cachePut.seconds();
            isOpen = true;
            return;
        }
    }

    /**
     * 获取缓存结果
     *
     * @return
     */
    public Object getResult() {
        return result;
    }

    /**
     * 将值放入缓存
     */
    public void setCache(Object value) {
        Element element = new Element(key, value);
        element.setTimeToIdle(seconds);
        ehcache.put(element);
    }

    /**
     * 判断是否已经打开缓存;  true/打开 false/没打开
     *
     * @return
     */
    public boolean isOpened() {
        return isOpen;
    }

    /**
     * 是否存在缓存; true/存在 false/不存在
     */
    public boolean existCache() {
        return exist;
    }

    /**
     * 自定义生成EhCache Key
     *
     * @param target
     * @param method
     * @param params
     * @return
     */
    private String encodeStr(Object target, Method method, Object... params) {
        String classesName = target.getClass().getSimpleName();
        String key = "[".concat(classesName).concat(".").concat(method.getName()).concat("(").concat(StrEncryptUtil.md5(JSON.toJSONString(params))).concat(")]");
        Log.debug("create cache key > {}", key);
        return key;
    }
}
