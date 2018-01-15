package cn.jants.common.exception;

import cn.jants.common.bean.Log;

/**
 * 该异常主要用于在Service层里面做抛出错误提示
 *
 * @author MrShun
 * @version 1.0
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