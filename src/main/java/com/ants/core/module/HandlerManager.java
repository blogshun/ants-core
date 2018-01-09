package com.ants.core.module;

import com.ants.common.enums.ResponseCode;
import com.ants.common.exception.SQLParamsException;
import com.ants.common.exception.TipException;
import com.ants.common.utils.RequestUtil;
import com.ants.common.utils.StrUtil;
import com.ants.core.ext.Handler;
import com.ants.restful.render.Json;
import com.mysql.jdbc.MysqlDataTruncation;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求链管理
 *
 * @author MrShun
 * @version 1.0
 * @date 2017-11-16
 */
final public class HandlerManager {

    private final List<Handler> handlers = new ArrayList<>();

    private Constant constant;

    public HandlerManager add(Handler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler can not be null");
        }
        handlers.add(handler);
        return this;
    }


    /**
     * 循环执行链表
     *
     * @return
     */
    public boolean execute(String target, HttpServletRequest request, HttpServletResponse response) {
        boolean isHandle = true;
        for (Handler handler : handlers) {
            try {
                isHandle = handler.preHandler(target, request, response);
                if (!isHandle) {
                    return false;
                }
            }
            //处理代理异常
            catch (InvocationTargetException e) {
                Throwable ex = e.getTargetException();
                ex.printStackTrace();
                errorWrite(request, response, ex);
                return false;
            }
            //处理Controller异常
            catch (Exception e) {
                e.printStackTrace();
                errorWrite(request, response, e);
                return false;
            }
        }
        return isHandle;
    }


    /**
     * 输出异常消息
     *
     * @param request
     * @param response
     * @param e
     */
    private void errorWrite(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        String requestType = request.getHeader("x-requested-with");
        Class<? extends Throwable> cls = e.getClass();
        //ajax 或者原生app
        if (RequestUtil.isAjax(request) || RequestUtil.isApp(request)) {
            //处理消息提示异常
            if (cls == TipException.class) {
                Json.writeJson(Json.exception(ResponseCode.ARGUMENTS_ERROR, e.getMessage()), response);
            }
            //处理数据库异常
            else if (cls == SQLParamsException.class) {
                Class sqlClass = ((SQLParamsException) e).getSqlException().getClass();
                //数据外键约束异常
                if (sqlClass == MySQLIntegrityConstraintViolationException.class) {
                    Json.writeJson(Json.exception(ResponseCode.RESTRIC_ERROR, "删除失败, 请先删除关联子数据!"), response);
                }//数据字段过长异常
                else if (sqlClass == MysqlDataTruncation.class) {
                    Json.writeJson(Json.exception(ResponseCode.COLUMN_LONG_ERROR, "某个数据字段, 内容超出!"), response);
                }
            } else if (cls == NullPointerException.class) {
                Json.writeJson(Json.exception(ResponseCode.NULL_POINT_ERROR), response);
            } else if (cls == IllegalArgumentException.class) {
                Json.writeJson(Json.exception(ResponseCode.ARGUMENTS_ERROR, e.getMessage()), response);
            } else {
                Json.writeJson(Json.exception(ResponseCode.UNKNOWN_ERROR, e.getMessage()), response);
            }
        } else {
            try {
                if (cls == FileNotFoundException.class) {
                    String error404Page = constant.getError404Page();
                    if(StrUtil.notBlank(error404Page)) {
                        request.getRequestDispatcher(error404Page).forward(request, response);
                    }
                } else {
                    String error500Page = constant.getError500Page();
                    if(StrUtil.notBlank(error500Page)) {
                        request.getRequestDispatcher(error500Page).forward(request, response);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setConstants(Constant constant) {
        this.constant = constant;
    }
}
