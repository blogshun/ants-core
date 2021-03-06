![Alt text](./src/main/resources/assets/ants-logo.png)
## Ants 极速开发框架

**只有在不断了解的过程中, 才能不断地进步**

无XML配置, 大小保持在800kb以下, 集成微信常用功能, 以及第三方授权登录;

轻薄封装, 依赖第三方jar包少[最烦为了一个小功能去依赖一个jar];

集成常用插件, 不像Spring那样什么插件都会集成, 主键大部分常用配置和SpringBoot差不多方便过渡到SpringBoot;

其核心设计目标是极速开发效率、代码量少、学习简单、配置简单、注解式restful、轻量级封装灵活度高;

内置包含轻量级Db操作 ORM Criteria, SqlMap 插件;

封装常用Ehcache缓存、Redis缓存、ActiveMq消息队列、scheduler任务调度、第三Eh方模板[Beetl、FreeMarker、Veloctiy]。


## maven 依赖

```xml
<!-- https://mvnrepository.com/artifact/cn.jants/ants-core -->
<dependency>
    <groupId>cn.jants</groupId>
    <artifactId>ants-core</artifactId>
    <version>1.0</version>
</dependency>
```


## Ants 核心模块

**Restful 模块**

支持动态参数URL匹配和SpringMVC一样，借助asm完成参数绑，还可以简单进行数据校验，支持form-data 和application/json参数绑定

**IOC 模块**

支持@Autowired、@Value注解、构造注入、支持afterPropertiesSet方法，基本和Spring IOC常用功能差不多, 只提前了常用的构造注入

**AOP模块**

将Spring Aop 5个通知状态，用一个方法来表示完成, spring底层也是一个方法切分的5个状态, 使用时需要配合Aop注解使用。

**Handler模块**

执行请求需要进行多层链过滤, 和Spring里的Handler差不多。

**Db模块**

轻薄封装借鉴Dbutils思想, 支持ORM Criteria操作和sql xml配置2种, 轻量级, 相当于spring jpa 和 mybatis

**Plugin模块**

集成常用数据源插件、缓存插件、和消息队列插件、SqlMap插件

另外内置tomcat容器，和jetty容器启动，支持jar模式运行，相较于SpringBoot体积更小，配置更加简单，

对多数据源和多数据源事物配置比较简单，Db更加接近底层，可选ORM操作数据库，或者SQLMap操作数据库等...

## 文档

文档URL地址 ： [点击这里](http://shunblog.cn/)

#### 文档目录

- [启动Demo](https://github.com/blogshun/ants-demos/README.md#启动Demo)
- [注解介绍](./DOC.md#mvc)
- [Restful](./DOC.md#安全控制)
- [参数绑定以及校验](./DOC.md#安全控制)
- 插件
	- [Template 模板引擎插件](./DOC.md#spi扩展)
	- [ORM Criteria](./DOC.md#jbootEvnet事件机制)
	- [SqlMap](./DOC.md#配置文件)
	- [Scheduler 任务调度](./DOC.md#代码生成器)
	- [Db 操作](./DOC.md#代码生成器)
	- [AcitveMq Jms](./DOC.md#代码生成器)
	- [Redis 缓存](./DOC.md#代码生成器)
	- [Ehcache 缓存](./DOC.md#代码生成器)
- [代码生成器](./DOC.md#项目构建)
- [联系作者](./DOC.md#联系作者)


## QQ群

Ants交流QQ群：创建中 ... , 欢迎加入讨论, 一起进行优化。
