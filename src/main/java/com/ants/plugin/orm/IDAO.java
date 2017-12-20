package com.ants.plugin.orm;

import com.ants.common.bean.IBean;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-07
 */
public interface IDAO<T extends IBean> {

    /**
     *
     * @param params
     * @return
     */
    T findById(T params);
}
