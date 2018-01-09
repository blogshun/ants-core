package com.ants.common.exception;

import com.ants.common.bean.Log;

/**
 * 该异常主要用于在Service层里面做抛出错误提示
 *
 * @author MrShun
 * @version 1.0
 * @date 2017-08-24
 */
public class TipException extends RuntimeException {

    public TipException() {
        super();
    }

    public TipException(String message) {
        super(message);
    }

    public TipException(Object obj, String message) {
        super(message);
    }

    public TipException(String message, Throwable cause) {
        super(message, cause);
    }

    public TipException(Throwable cause) {
        super(cause);
    }

    @Override
    public void printStackTrace() {
        StackTraceElement stackTraceElement = getStackTrace()[0];
        Log.info("提示：" + stackTraceElement.toString() + " -> " + getMessage());
    }
}
