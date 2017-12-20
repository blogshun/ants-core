package com.ants.plugin.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017/11/25
 */
public class EhCacheTpl {

    private CacheManager cacheManager;

    /**
     * 默认缓存名称
     */
    public final static String DEFAULT_CACHE = "defaultCache";


    public EhCacheTpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * 放入缓存值
     *
     * @param cacheName 缓存器名称
     * @param key       键
     * @param value     值
     * @param value     时间 秒
     */
    public void put(String cacheName, String key, Object value, Integer seconds){
        if(cacheName == null){
            cacheName = DEFAULT_CACHE;
        }
        Cache cache = cacheManager.getCache(cacheName);
        Element element = new Element(key, value);
        if(seconds != null){
            element.setTimeToIdle(seconds);
        }
        cache.put(element);
    }

    public void put(String cacheName, String key, Object value) {
        put(cacheName, key, value, null);
    }

    public void put(String key, Object value, Integer seconds) {
        put(null, key, value, seconds);
    }

    public void put(String key, Object value) {
        put(null, key, value, null);
    }
    /**
     * 根据键获取值
     *
     * @param cacheName 缓存器名称
     * @param key       键
     * @return
     */
    public Object get(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        Element element = cache.get(key);
        return element == null ? null : element.getObjectValue();
    }

    public Object get(String key) {
        return get(DEFAULT_CACHE, key);
    }

    /**
     * 根据键移除掉值
     *
     * @param cacheName 缓存器名称
     * @param key       键
     */
    public void remove(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.remove(key);
    }

    public void remove(String key) {
        remove(DEFAULT_CACHE, key);
    }

    /**
     * 得到缓存器对象
     *
     * @param cacheName 缓存名称
     * @return
     */
    public Cache getCache(String cacheName) {
        return cacheManager.getCache(cacheName);
    }

    public Cache getCache() {
        return getCache(DEFAULT_CACHE);
    }

    /**
     * 得到缓存器所有键
     *
     * @param cacheName 缓存名称
     * @return
     */
    public List<String> getKeys(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        return cache.getKeys();
    }

    public List<String> getKeys() {
        return getKeys(DEFAULT_CACHE);
    }


    public void clear(String prefix){
        cacheManager.clearAllStartingWith(prefix);
    }
}
