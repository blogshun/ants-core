package com.ants.common.exception;

import com.ants.common.bean.Log;

import java.sql.SQLException;

/**
 * SQL异常可以扩展
 *
 * @author MrShun
 * @version 1.0
 * Date 2017-08-27
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
