package com.ants.common.annotation.boot;

import com.ants.common.enums.LoadType;
import com.ants.common.enums.ViewType;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017/12/18
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
