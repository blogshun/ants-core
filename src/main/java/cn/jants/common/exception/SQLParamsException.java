package cn.jants.common.exception;

import cn.jants.common.bean.Log;

import java.sql.SQLException;

/**
 * SQL异常可以扩展
 *
 * @author MrShun
 * @version 1.0
 */
public class SQLParamsException extends RuntimeException {

    private SQLException sqlException;

    public SQLParamsException(SQLException sqlException) {
        this.sqlException = sqlException;
    }

    public SQLException getSqlException() {
        return sqlException;
    }

    @Override
    public void printStackTrace() {
        StackTraceElement stackTraceElement = getStackTrace()[0];
        Log.error("错误提示：" + stackTraceElement.toString() + " -> " + sqlException.getMessage());
    }
}
