package cn.jants.common.annotation.boot;

import cn.jants.common.enums.LoadType;
import cn.jants.common.enums.ViewType;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewConfiguration {

    ViewType viewType() default ViewType.JSP;

    String loadPath() default "";

    String suffix() default "";

    /**
     * @return 更新时间默认是10小时, 单位是秒
     */
    int updateDelay() default 6000;

    String encoding() default "UTF-8";

    /**
     * @return 默认从Resource加载
     */
    LoadType loadType() default LoadType.Resource;

}
