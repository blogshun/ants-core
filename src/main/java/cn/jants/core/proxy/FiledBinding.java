package cn.jants.core.proxy;

import cn.jants.common.annotation.service.Autowired;
import cn.jants.common.enums.DataSourceType;
import cn.jants.core.module.ServiceManager;
import cn.jants.restful.bind.utils.ReflectionUtils;
import cn.jants.common.annotation.service.Source;
import cn.jants.common.annotation.service.Value;
import cn.jants.common.bean.Log;
import cn.jants.common.bean.Prop;
import cn.jants.core.ext.InitializingBean;
import cn.jants.core.module.DbManager;
import cn.jants.core.utils.GenerateUtil;
import cn.jants.core.utils.ParamTypeUtil;
import cn.jants.plugin.db.Db;

import javax.sql.DataSource;
import java.lang.reflect.Field;

/**
 * 类里面的属性字段绑定
 *
 * @author MrShun
 * @version 1.0
 */
public class FiledBinding {

    public static void initFiledValues(Object object) {
        Class cls = object.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                ReflectionUtils.makeAccessible(field);
                String key = "";
                Object service;
                try {
                    Class targetClass = field.getDeclaredAnnotation(Autowired.class).value();
                    Class typeClass = field.getType();
                    //判断是接口还是实现类
                    if (typeClass.isInterface() && targetClass == Autowired.class) {
                        //默认直接实例化第一个接口
                        service = ServiceManager.getService(typeClass);
                        if (service == null) {
                            throw new RuntimeException(cls + " 中含有@Service注解类中, 不能注入接口!");
                        }
                    } else {
                        String cName = targetClass == Autowired.class ? typeClass.getName() : targetClass.getName();
                        key = GenerateUtil.createServiceKey(cName);
                        service = ServiceManager.getService(key);
                        if (service == null) {
                            service = Class.forName(cName).newInstance();
                            FiledBinding.initFiledValues(service);
                            //采用CGLIB代理
                            Object proxy = CglibProxy.createProxy(service);
                            //初始化完成后调用init()实例化
                            if (proxy instanceof InitializingBean) {
                                InitializingBean ib = (InitializingBean) service;
                                ib.afterPropertiesSet();
                            }
                            ServiceManager.setService(key, proxy);
                        }
                    }
                    field.set(object, service);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    throw new RuntimeException(cls + " " + key + " 实例化失败, 请认真检查!");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (field.isAnnotationPresent(Value.class)) {
                try {
                    Value v = field.getAnnotation(Value.class);
                    Object paramValue = Prop.getKeyValue(cls.getName(), v.value());
                    Object objectValue = ParamTypeUtil.parse(String.valueOf(paramValue), field.getType());
                    ReflectionUtils.makeAccessible(field);
                    field.set(object, objectValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (field.isAnnotationPresent(Source.class) && field.getType() == Db.class) {
                try {
                    ReflectionUtils.makeAccessible(field);
                    Source source = field.getAnnotation(Source.class);
                    if ("".equals(source.value()) && source.type() == DataSourceType.NONE) {
                        if(DbManager.containsKey("")){
                            field.set(object, DbManager.get(""));
                        }else {
                            Db db = new Db();
                            field.set(object, db);
                            DbManager.add("", db);
                        }
                    } else {
                        Db db = DbManager.get(source.value());
                        if(db == null) {
                            if (source.type() == DataSourceType.NONE) {
                                db = DataSourceType.getNativeDb(source.value());
                            } else {
                                DataSource dataSource = DataSourceType.getDataSource(source);
                                if (dataSource == null) {
                                    Log.warn("@Source 指定了 {} 数据源, 却没有进行配置!", source.type());
                                    db = new Db();
                                } else {
                                    db = new Db(dataSource);
                                }
                            }
                            DbManager.add(source.value(), db);
                        }
                        field.set(object, db);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
