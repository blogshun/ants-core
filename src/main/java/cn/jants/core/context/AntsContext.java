package cn.jants.core.context;

import cn.jants.common.annotation.boot.DbConfiguration;
import cn.jants.common.annotation.boot.DbSource;
import cn.jants.common.annotation.boot.PropertyConfiguration;
import cn.jants.common.annotation.boot.ViewConfiguration;
import cn.jants.common.annotation.plugin.EnableActiveMQPlugin;
import cn.jants.common.annotation.plugin.EnableEhcachePlugin;
import cn.jants.common.annotation.plugin.EnableRedisPlugin;
import cn.jants.common.annotation.plugin.EnableSQLMapPlugin;
import cn.jants.common.annotation.service.Application;
import cn.jants.common.bean.Log;
import cn.jants.common.bean.Prop;
import cn.jants.common.enums.DataSourceType;
import cn.jants.common.enums.ViewType;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.ext.Plugin;
import cn.jants.core.handler.RenderHandler;
import cn.jants.core.module.*;
import cn.jants.core.utils.ScanUtil;
import cn.jants.plugin.cache.EhCachePlugin;
import cn.jants.plugin.cache.RedisPlugin;
import cn.jants.plugin.db.*;
import cn.jants.plugin.jms.ActiveMqPlugin;
import cn.jants.plugin.jms.ConsumerManager;
import cn.jants.plugin.jms.JmsListener;
import cn.jants.plugin.scheduler.FixedDelay;
import cn.jants.plugin.scheduler.SchedulerBean;
import cn.jants.plugin.scheduler.SchedulerPlugin;
import cn.jants.plugin.sqlmap.SqlMapPlugin;
import cn.jants.plugin.template.BeetleTpl;
import cn.jants.plugin.template.FreeMarkerTpl;
import cn.jants.plugin.template.VelocityTpl;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 核心加载器
 *
 * @author MrShun
 * @version 1.0
 *          Date 2017-11-19
 */
public class AntsContext {

    /**
     * 常量
     */
    private final Constant constant = new Constant();

    /**
     * 插件
     */
    private final PluginManager plugins = new PluginManager();

    /**
     * 拦截器
     */
    private final InterceptorManager interceptors = new InterceptorManager();

    /**
     * handler链
     */
    private final HandlerManager handlers = new HandlerManager();

    /**
     * 任务调度
     */
    private SchedulerPlugin schedulers;

    /**
     * 基于注解初始化
     *
     * @param loadClass      加载类
     * @param servletContext
     */
    public AntsContext(Class<?> loadClass, ServletContext servletContext) {
        AppConstant.LOAD_CLASS = loadClass;
        //通过注解加载配置
        PropertyConfiguration propertyConfiguration = loadClass.getDeclaredAnnotation(PropertyConfiguration.class);
        if (propertyConfiguration != null) {
            //初始化AES 16位随机密钥
            String secretKey = propertyConfiguration.secretKey();
            if (StrUtil.notBlank(secretKey)) {
                if (secretKey.length() != 16) {
                    throw new RuntimeException("secretKey must be 16 digit.");
                }
                AppConstant.SECRET_KEY = secretKey;
            }
            //加载配置文件
            String[] props = propertyConfiguration.value();
            Prop.use(props.length == 0 ? new String[]{AppConstant.DEFAULT_CONFIG} : props);

            //是否是调试模式
            AppConstant.DEBUG = propertyConfiguration.debug();
            constant.setError404Page(propertyConfiguration.page404());
            constant.setError500Page(propertyConfiguration.page500());
            constant.setRegexSuffix(propertyConfiguration.suffix());
            constant.setResources(propertyConfiguration.resources());
            constant.setEncoding(propertyConfiguration.encoding());

            //设置全局后缀
            String regexSuffix = constant.getRegexSuffix();
            if (!"{:()}".equals(regexSuffix)) {
                AppConstant.URL_REGEX_SUFFIX = regexSuffix;
            }

            //设置运行域
            AppConstant.DOMAIN = propertyConfiguration.domain();
        }

        //通过注解获取模板配置
        ViewConfiguration tplConfiguration = loadClass.getDeclaredAnnotation(ViewConfiguration.class);
        if (tplConfiguration != null) {
            AppConstant.TPL_CONFIG = tplConfiguration;

            //初始化相应的模板对象
            if (tplConfiguration.viewType() == ViewType.FREEMARKER) {
                ServiceManager.setService("plugin_template_FreeMarkerTpl", new FreeMarkerTpl(tplConfiguration));
            } else if (tplConfiguration.viewType() == ViewType.BEETL) {
                ServiceManager.setService("plugin_template_BeetleTpl", new BeetleTpl(tplConfiguration));
            } else if (tplConfiguration.viewType() == ViewType.VELOCITY) {
                ServiceManager.setService("plugin_template_VelocityTpl", new VelocityTpl(tplConfiguration));
            }
        }

        if (loadClass.getSuperclass().equals(AppConfiguration.class)) {
            //初始化
            initAppConfiguration(loadClass);
        }

        //获取需要扫描的包路径
        Application application = loadClass.getDeclaredAnnotation(Application.class);
        if (application == null) {
            throw new RuntimeException("启动类缺少@Application注解");
        }
        String[] packages = application.scanPackages();
        String defaultPath = loadClass.getName();
        int lastNum = defaultPath.lastIndexOf(".");
        String[] defaultPackage = {""};
        if (lastNum != -1) {
            defaultPackage[0] = defaultPath.substring(0, lastNum);
        }

        if (AppConstant.DEBUG) {
            Log.debug("Loading Plugins .....");
        }

        String[] pgs = packages.length == 0 ? defaultPackage : packages;

        //初始化任务调度
        schedulers = initSchedulerPlugin(pgs);

        if (AppConstant.DEBUG) {
            Log.debug("初始化注解插件 .....");
        }
        //初始化注解插件
        initAnnotationPlugin(loadClass);

        //初始化插件
        startPlugins();

        //注册Service
        if (AppConstant.DEBUG) {
            Log.debug("Register Service .....");
        }
        ServiceManager.register(pgs);

        //注册RequestMapping, 请求绑定类对象
        if (AppConstant.DEBUG) {
            Log.debug("Register Controller .....");
        }
        RequestMappingManager.register(pgs);

        //开启所有调度任务, 必须在service之后
        if (schedulers != null) {
            schedulers.start();
        }
        //检查是否有Jms消费者
        initJmsConsumer(pgs, loadClass);
    }

    /**
     * 得到常量
     *
     * @return
     */
    public Constant getConstant() {
        return constant;
    }

    /**
     * 得到handler链表
     *
     * @return
     */
    public HandlerManager getHandlerManager() {
        handlers.setConstants(constant);
        //添加最后一个handler链, 作为渲染
        handlers.add(new RenderHandler(constant));
        return handlers;
    }

    /**
     * 初始化任务调度管理器, 返回管理器对象
     *
     * @param packages
     * @return
     */
    private SchedulerPlugin initSchedulerPlugin(String[] packages) {
        List<Class<?>> classes = ScanUtil.findScanClass(packages, FixedDelay.class);
        if (classes == null || classes.size() == 0) {
            return null;
        }
        List<SchedulerBean> schedulers = new ArrayList<>();
        for (Class<?> cls : classes) {
            FixedDelay fixedDelay = cls.getDeclaredAnnotation(FixedDelay.class);
            schedulers.add(new SchedulerBean(fixedDelay, cls));
        }
        return new SchedulerPlugin(schedulers);
    }

    /**
     * 初始化注解插件
     */
    private void initAnnotationPlugin(Class<?> loadClass) {
        //加载数据库链接
        DbConfiguration dbConfiguration = loadClass.getDeclaredAnnotation(DbConfiguration.class);
        if (dbConfiguration != null) {
            DbSource[] dbs = dbConfiguration.dbs();
            if (dbs.length > 0) {
                for (DbSource db : dbs) {
                    if (db.sourceType() == DataSourceType.DRUID) {
                        plugins.add(new DruidPlugin(Prop.getKeyStrValue(db.name())
                                , Prop.getKeyStrValue(db.url())
                                , Prop.getKeyStrValue(db.driver())
                                , Prop.getKeyStrValue(db.username())
                                , Prop.getKeyStrValue(db.password())));
                    } else if (db.sourceType() == DataSourceType.C3P0) {
                        plugins.add(new C3p0Plugin(Prop.getKeyStrValue(db.name())
                                , Prop.getKeyStrValue(db.url())
                                , Prop.getKeyStrValue(db.driver())
                                , Prop.getKeyStrValue(db.username())
                                , Prop.getKeyStrValue(db.password())));
                    } else if (db.sourceType() == DataSourceType.DBCP) {
                        plugins.add(new DbcpPlugin(Prop.getKeyStrValue(db.name())
                                , Prop.getKeyStrValue(db.url())
                                , Prop.getKeyStrValue(db.driver())
                                , Prop.getKeyStrValue(db.username())
                                , Prop.getKeyStrValue(db.password())));
                    } else if (db.sourceType() == DataSourceType.HIKARICP) {
                        plugins.add(new HikariCpPlugin(Prop.getKeyStrValue(db.name())
                                , Prop.getKeyStrValue(db.url())
                                , Prop.getKeyStrValue(db.driver())
                                , Prop.getKeyStrValue(db.username())
                                , Prop.getKeyStrValue(db.password())));
                    } else {
                        plugins.add(new DbPlugin(Prop.getKeyStrValue(db.name())
                                , Prop.getKeyStrValue(db.url())
                                , Prop.getKeyStrValue(db.driver())
                                , Prop.getKeyStrValue(db.username())
                                , Prop.getKeyStrValue(db.password())));
                    }
                }
            }
        }

        //初始化Reids插件
        EnableRedisPlugin redisPlugin = loadClass.getDeclaredAnnotation(EnableRedisPlugin.class);
        if (redisPlugin != null) {
            String hostStr = Prop.getKeyStrValue(redisPlugin.host());
            String defaultHostStr = Prop.getStr("ants.redis.host");
            String host = StrUtil.notBlank(hostStr) ? hostStr : defaultHostStr;

            Integer portStr = Prop.getKeyIntValue(redisPlugin.port());
            Integer defaultPortStr = Prop.getInt("ants.redis.port");
            Integer port = StrUtil.notNull(portStr) ? portStr : defaultPortStr;

            Integer dataBaseStr = Prop.getKeyIntValue(redisPlugin.database());
            Integer defaultDataBaseStr = Prop.getInt("ants.redis.database");
            Integer database = StrUtil.notNull(dataBaseStr) ? dataBaseStr : defaultDataBaseStr;

            String passwordStr = Prop.getKeyStrValue(redisPlugin.password());
            String defaultPasswordStr = Prop.getStr("ants.redis.password");
            String password = StrUtil.notBlank(passwordStr) ? passwordStr : defaultPasswordStr;
            plugins.add(new RedisPlugin(host
                    , port
                    , database
                    , password));
        }

        //初始化ActiveMq生产者插件
        EnableActiveMQPlugin activeMQPlugin = loadClass.getDeclaredAnnotation(EnableActiveMQPlugin.class);
        if (activeMQPlugin != null) {
            String brokerUrlStr = Prop.getKeyStrValue(activeMQPlugin.brokerUrl());
            String defaultBrokerUrlStr = Prop.getStr("ants.jms.activemq.broker-url");
            String brokerUrl = StrUtil.notBlank(brokerUrlStr) ? brokerUrlStr : defaultBrokerUrlStr;


            String userNameStr = Prop.getKeyStrValue(activeMQPlugin.username());
            String defaultUserNameStr = Prop.getStr("ants.jms.activemq.username");
            String username = StrUtil.notBlank(userNameStr) ? userNameStr : defaultUserNameStr;

            String passwordStr = Prop.getKeyStrValue(activeMQPlugin.password());
            String defaultPasswordStr = Prop.getStr("ants.jms.activemq.password");
            String password = StrUtil.notBlank(passwordStr) ? passwordStr : defaultPasswordStr;
            plugins.add(new ActiveMqPlugin(brokerUrl
                    , username
                    , password));
        }

        //初始化Ehcache插件
        EnableEhcachePlugin enableEhcachePlugin = loadClass.getDeclaredAnnotation(EnableEhcachePlugin.class);
        if (enableEhcachePlugin != null) {
            plugins.add(new EhCachePlugin(enableEhcachePlugin.value()));
        }

        //初始化SqlMap插件
        EnableSQLMapPlugin sqlMapPlugin = loadClass.getDeclaredAnnotation(EnableSQLMapPlugin.class);
        if (sqlMapPlugin != null) {
            plugins.add(new SqlMapPlugin(sqlMapPlugin.value()));
            //注册Mapper
            if (AppConstant.DEBUG) {
                Log.debug("Register Mapper .....");
            }
            if(sqlMapPlugin.hump()){
                AppConstant.HUMP = true;
            }
            Package pgs = loadClass.getPackage();
            MapperManager.register(pgs.getName());
        }

    }

    /**
     * 初始化Jms消费者
     *
     * @param packages
     */
    private void initJmsConsumer(String[] packages, Class<?> loadClass) {
        //初始化ActiveMq生产者插件
        EnableActiveMQPlugin activeMQPlugin = loadClass.getDeclaredAnnotation(EnableActiveMQPlugin.class);
        if (activeMQPlugin != null) {
            String brokerUrlStr = Prop.getKeyStrValue(activeMQPlugin.brokerUrl());
            String defaultBrokerUrlStr = Prop.getStr("ants.jms.activemq.broker-url");
            String brokerUrl = StrUtil.notBlank(brokerUrlStr) ? brokerUrlStr : defaultBrokerUrlStr;


            String userNameStr = Prop.getKeyStrValue(activeMQPlugin.username());
            String defaultUserNameStr = Prop.getStr("ants.jms.activemq.username");
            String username = StrUtil.notBlank(userNameStr) ? userNameStr : defaultUserNameStr;

            String passwordStr = Prop.getKeyStrValue(activeMQPlugin.password());
            String defaultPasswordStr = Prop.getStr("ants.jms.activemq.password");
            String password = StrUtil.notBlank(passwordStr) ? passwordStr : defaultPasswordStr;
            List<Class<?>> classes = ScanUtil.findScanClass(packages, JmsListener.class);
            if (classes != null && classes.size() > 0) {
                new ConsumerManager(brokerUrl, username, password, classes).start();
            }
        }

    }

    private void initAppConfiguration(Class<?> loadClass) {
        try {
            AppConfiguration appConfiguration = (AppConfiguration) loadClass.newInstance();
            //初始化常量配置
            appConfiguration.configConstant(constant);
            //初始化插件
            appConfiguration.configPlugin(plugins);
            //初始化链
            appConfiguration.configHandler(handlers);
            //初始化拦截器
            appConfiguration.configInterceptor(interceptors);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启所有插件
     */
    private void startPlugins() {
        List<Plugin> pluginList = PluginManager.getPluginList();
        if (pluginList == null) {
            return;
        }
        for (Plugin plugin : pluginList) {
            try {
                if (plugin.start() == false) {
                    String message = "Plugin start error: " + plugin.getClass().getName();
                    Log.error(message);
                    throw new RuntimeException(message);
                }
            } catch (Exception e) {
                String message = "Plugin start error: " + plugin.getClass().getName() + ". \n" + e.getMessage();
                Log.error(message);
                throw new RuntimeException(message, e);
            }
        }
        if (AppConstant.DEBUG) {
            Log.debug(">>> 共计 {} 个插件", pluginList.size());
        }
    }

    /**
     * 停止所有插件
     */
    public void stopPlugins() {
        if (schedulers != null) {
            schedulers.destroy();
        }
        List<Plugin> pluginList = PluginManager.getPluginList();
        if (pluginList != null) {
            for (int i = pluginList.size() - 1; i >= 0; i--) {
                boolean success = false;
                try {
                    success = pluginList.get(i).destroy();
                } catch (Exception e) {
                    success = false;
                    Log.error(e.getMessage());
                }
                if (!success) {
                    System.err.println("Plugin stop error: " + pluginList.get(i).getClass().getName());
                }
            }
        }
    }
}
