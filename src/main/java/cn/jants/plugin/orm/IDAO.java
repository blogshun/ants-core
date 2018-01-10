package cn.jants.plugin.orm;

import cn.jants.common.bean.IBean;

/**
 * @author MrShun
 * @version 1.0
 */
public interface IDAO<T extends IBean> {

    /**
     *
     * @param params
     * @return
     */
    T findById(T params);
}
