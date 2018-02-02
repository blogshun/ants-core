package cn.jants.core.module;

import cn.jants.plugin.db.Db;
import cn.jants.plugin.sqlmap.annotation.Mapper;
import cn.jants.common.bean.Log;
import cn.jants.core.context.AppConstant;
import cn.jants.plugin.sqlmap.MapperProxy;
import cn.jants.core.utils.ScanUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
final public class MapperManager {

    private static final ConcurrentMap<String, Object> MAPPERS = new ConcurrentHashMap<>();

    /**
     * 注册Mapper
     *
     * @param packages 包路径
     */
    public static void register(String... packages) {
        List<Class<?>> mapperCls = ScanUtil.findScanClass(packages, Mapper.class);
        if(mapperCls.size() > 0){
            Db db = new Db();
            DbManager.add(DbManager.DEFAULT_NAME, db);
        }
        for (Class<?> cls: mapperCls) {
            String simpleName = cls.getSimpleName();
            if (AppConstant.DEBUG) {
                Log.debug(">>> {} :: Generator Success !", simpleName);
            }
            MapperProxy mapperProxy = new MapperProxy(simpleName, cls);
            MAPPERS.put(simpleName, mapperProxy.getProxy());
        }
        if (AppConstant.DEBUG) {
            Log.debug(">>> 共计 {} 个Mapper", mapperCls.size());
        }
    }

    public static Object getMapper(Class cls){
        return MAPPERS.get(cls.getSimpleName());
    }
}
