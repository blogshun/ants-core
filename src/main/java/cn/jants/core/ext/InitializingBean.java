package cn.jants.core.ext;


/**
 * 初始化实例的时候执行，在构造之后
 *
 * @author MrShun
 * @version 1.0
 */
public interface InitializingBean {

    /**
     * 执行init方法
     */
    void afterPropertiesSet();
}
