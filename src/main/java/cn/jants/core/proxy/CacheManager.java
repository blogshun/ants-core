package cn.jants.core.proxy;

import cn.jants.common.bean.Log;
import cn.jants.common.utils.StrEncryptUtil;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.module.ServiceManager;
import cn.jants.plugin.cache.CacheEvict;
import cn.jants.plugin.cache.CachePut;
import cn.jants.plugin.cache.Cacheable;
import cn.jants.plugin.cache.EhCacheTpl;
import cn.jants.restful.bind.LocalVariableTableParameterNameDiscoverer;
import cn.jants.restful.bind.utils.ReflectionUtils;
import com.alibaba.fastjson.JSON;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class CacheManager {

    private final static LocalVariableTableParameterNameDiscoverer LVP = new LocalVariableTableParameterNameDiscoverer();

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

    /**
     * key变量符
     */
    private String keySymbol = "#";

    public CacheManager(Object target, Method method, Object[] args) {
        //存入缓存
        Cacheable cache = method.getDeclaredAnnotation(Cacheable.class);
        if (cache != null) {
            isOpen = true;
            key = StrUtil.notBlank(cache.key()) ? getVarKey(cache.key(), target, method, args) : encodeStr(target, method, args);
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
            key = cacheEvict.key();
            if (StrUtil.notBlank()) {
                ehcache.remove(key);
            } else {
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
            key = StrUtil.notBlank(cachePut.key()) ? getVarKey(cachePut.key(), target, method, args) : encodeStr(target, method, args);
            if (StrUtil.notBlank()) {
                ehcache.remove(key);
            } else {
                ehcache.removeAll();
            }
            seconds = cachePut.seconds();
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

    private String getVarKey(String key, Object target, Method method, Object... params) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        //处理有变量Key
        else if (key.indexOf(keySymbol) != -1) {
            int ksNum = key.indexOf(keySymbol);
            String prefix = "";
            if (ksNum > 0) {
                prefix = key.split(keySymbol)[0];
                key = key.substring(ksNum + 1, key.length());
            } else {
                key = key.substring(1, key.length());
            }
            int s = key.indexOf(".");
            List<String> parameterNames = Arrays.asList(LVP.getParameterNames(method));
            String genKey = "";
            //是对象时
            if (s != -1 && s != 0 && s != key.length() - 1) {
                String[] ps = key.split("\\.");
                int i = parameterNames.indexOf(ps[0]);
                if (i == -1) {
                    return null;
                }
                Object op = params[i];
                Class<?> cls = op.getClass();
                try {
                    Field field = cls.getDeclaredField(ps[1]);
                    ReflectionUtils.makeAccessible(field);
                    genKey = String.valueOf(ReflectionUtils.getField(field, op));
                } catch (NoSuchFieldException e) {
                    throw new IllegalArgumentException(String.format("%s 没找到需要绑定的实体字段 -> %s", cls.getSimpleName(), ps[1]));
                }
            } else {
                int i = parameterNames.indexOf(key);
                if (i == -1) {
                    return null;
                }
                genKey = String.valueOf(params[i]);
            }
            String simpleName = target.getClass().getSimpleName();
            return prefix.concat("_").concat(simpleName).concat("(").concat(genKey).concat(")");
        }
        return key;
    }
}
