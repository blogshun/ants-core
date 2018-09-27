package cn.jants.common.enums;

/**
 * 响应码定义
 * Created by MrShun on 2016/7/15.
 */
public enum ResponseCode {

    SUCCESS(0, "ok"),

    //token is expired
    TOKEN_EXPIRED_INFO(1000, "Token令牌已经过期!"),

    //token is invalid
    TOKEN_INVALID_INFO(1001, "无效的Token令牌!"),

    //no operation authority
    AUTH_INFO(1002, "您没有操作权限!"),

    SING_ERROR(1003, "signature 签名错误!"),

    REQUEST_INVALID_ERROR(1007, "该请求已失效!"),

    MYSQL_PK_ERROR(1004, "该记录被引用，操作失败!"),

    FILE_MAX_INFO(1005, "上传文件超出指定大小!"),

    REFRESH_INFO(1006, "你的刷新速度太快了!"),

    REQUEST_ERROR(2001, "500 program internal error"),

    DATA_NULL_ERROR(2002, "response data is null"),

    REQUEST_TYPE_ERROR(2003, "405 request type error"),

    NULL_POINT_ERROR(2004, "object null pointer error"),

    ARGUMENTS_ERROR(2005, "request argument is error"),

    UNKNOWN_ERROR(2006, "unknown error"),

    RECORD_REF_ERROR(3001, "该记录已被其他数据引用, 操作失败!"),

    COLUMN_LONG_ERROR(3002, "提交的数据长度超出!"),

    SQL_PARAM_ERROR(3003, "sql params exception"),

    CONTENT_GARBAGE_INFO(4001, "提交文本内容包含非法信息!"),

    IMAGE_GARBAGE_INFO(4002, "提交图片内容包含非法信息!");

    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
