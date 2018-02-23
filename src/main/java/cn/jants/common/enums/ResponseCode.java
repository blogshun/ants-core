package cn.jants.common.enums;

/**
 * 响应码定义
 * Created by MrShun on 2016/7/15.
 */
public enum ResponseCode {

    SUCCESS(0, "ok"),

    //token is expired
    TOKEN_EXPIRED_INFO(100, "Token令牌已经过期!"),

    //token is invalid
    TOKEN_INVALID_INFO(101, "无效的Token令牌!"),

    //no operation authority
    AUTH_INFO(102, "您没有操作权限!"),

    LOGIN_INFO(103, "请再次登录!"),

    MYSQL_PK_ERROR(104, "该记录被引用，操作失败!"),

    FILE_MAX_INFO(105, "上传文件超出指定大小!"),

    REFRESH_INFO(106, "你的刷新速度太快了!"),

    REQUEST_ERROR(107, "500 program internal error"),

    DATA_NULL_ERROR(109, "response data is null"),

    REQUEST_TYPE_ERROR(405, "405 request type error"),

    NULL_POINT_ERROR(1000, "object null pointer error"),

    ARGUMENTS_ERROR(1011, "request argument is error"),

    UNKNOWN_ERROR(1012, "unknown error"),

    RESTRIC_ERROR(2001, "该记录已被其他数据引用, 操作失败!"),

    COLUMN_LONG_ERROR(2002, "提交的数据长度超出!"),

    SQL_PARAM_ERROR(2003, "sql params exception");

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
