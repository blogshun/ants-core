package cn.jants.common.exception;

import cn.jants.common.bean.Log;
import cn.jants.common.enums.ResponseCode;

/**
 * 该异常主要用于在Service层里面做抛出错误提示
 *
 * @author MrShun
 * @version 1.0
 */
public class TipException extends RuntimeException {

    private Integer code;

    private String msg;

    public TipException() {
        super();
    }

    public TipException(String msg) {
        this.msg = msg;
    }

    public TipException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }

    public TipException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public TipException(String message, Throwable cause) {
        super(message, cause);
    }

    public TipException(Throwable cause) {
        super(cause);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void printStackTrace() {
        if (msg != null) {
            StackTraceElement stackTraceElement = getStackTrace()[0];
            Log.info("提示：" + stackTraceElement.toString() + " -> " + msg);
        }
    }
}
