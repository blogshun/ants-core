package cn.jants.common.enums;

/**
 * 响应码定义
 * Created by MrShun on 2016/7/15.
 */
public enum ResponseCode {

    SUCCESS(0, "ok"),

    TOKEN_EXPIRED_INFO(100, "token is expired"),

    TOKEN_INVALID_INFO(101, "token is invalid"),

    AUTH_INFO(102, "no operation authority"),

    LOGIN_INFO(103, "please log in again"),

    MYSQL_PK_ERROR(104, "the record was referenced, and the operation failed"),

    FILE_MAX_INFO(105, "upload file size out of range"),

    REFRESH_INFO(106, "your refresh is too fast to stand"),

    REQUEST_ERROR(107, "500 program internal error"),

    DATA_NULL_ERROR(109, "response data is null"),

    REQUEST_TYPE_ERROR(405, "405 request type error"),

    NULL_POINT_ERROR(1000, "object null pointer error"),

    ARGUMENTS_ERROR(1011, "request argument is error"),

    UNKNOWN_ERROR(1012, "unknown error"),

    RESTRIC_ERROR(2001, "table data is associated with restrictions"),

    COLUMN_LONG_ERROR(2002, "data too long for column"),

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
