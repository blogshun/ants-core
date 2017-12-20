![Alt text](./src/main/resources/assets/ants-logo.png)
## Ants 极速开发框架

**只有在不断了解的过程中, 才能不断地进步**

吸取SpringBoot大部分精华功能的同时简化SpringBoot部分配置, 轻薄封装, 依赖第三方jar包少;

集成常用插件, 不像Spring那样什么插件都会集成, 后面文档会介绍自定义插件, 授人鱼不如授人以渔;

其核心设计目标是极速开发效率、代码量少、学习简单、配置简单、注解式restful、轻量级封装灵活度高;

内置包含轻量级Db操作 ORM Criteria, SqlMap 插件;

封装常用Ehcache缓存、Redis缓存、ActiveMq消息队列、scheduler任务调度、第三方模板[Beetl、FreeMarker、Veloctiy]。

有人可能会问为啥不集成更多插件, 我觉得没有必要, 集成插件本来就不是很复杂的工作量, 集成太多只会导致项目更加臃肿
, 后期demos项目里面会集成其他一些插件

后期有时间会介绍此框架整体的一个构架以及原理, 让人人都可以熟悉了解编写一个框架的过程, 文档和demo后期会补上

QQ交流群： 创建中 ...

## maven 依赖

```xml
正在构建中 ...
```


## Ants 核心模块

**Restful 模块**

支持动态参数URL匹配和SpringMVC一样，借助asm完成参数绑，还可以简单进行数据校验定，支持form-data 和application/json参数绑定

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
- [MVC](./DOC.md#mvc)
- [安全控制](./DOC.md#安全控制)
- [ORM](./DOC.md#orm)
- [AOP](./DOC.md#aop)
- [RPC](./DOC.md#rpc远程调用)
- [MQ](./DOC.md#mq消息队列)
- [Cache](./DOC.md#cache缓存)
- [http客户端](./DOC.md#http客户端)
- [metrics数据监控](./DOC.md#metrics数据监控)
- [容错与隔离](./DOC.md#容错与隔离)
- [Opentracing数据追踪](./DOC.md#opentracing数据追踪)
- [统一配置中心](./DOC.md#统一配置中心)
- [Swagger api](./DOC.md#swagger-api自动生成)
- 其他
	- [SPI扩展](./DOC.md#spi扩展)
	- [JbootEvnet事件机制](./DOC.md#jbootEvnet事件机制)
	- [配置文件](./DOC.md#配置文件)
	- [代码生成器](./DOC.md#代码生成器)
- [项目构建](./DOC.md#项目构建)
- [联系作者](./DOC.md#联系作者)


## QQ群

Ants交流QQ群：创建中 ... , 欢迎加入讨论, 一起进行优化。