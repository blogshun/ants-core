package com.ants.core.handler;

import com.ants.common.bean.Log;
import com.ants.common.enums.RequestMethod;
import com.ants.common.enums.ResponseCode;
import com.ants.core.context.AppConstant;
import com.ants.core.ext.Handler;
import com.ants.core.holder.ClientRequest;
import com.ants.core.holder.ContextRequestManager;
import com.ants.core.module.Constant;
import com.ants.core.module.RequestMappingManager;
import com.ants.restful.render.Json;
import com.ants.restful.render.ModelAndView;
import com.ants.restful.render.Resource;
import com.ants.restful.render.View;
import com.ants.restful.request.BindingParams;
import com.ants.restful.request.MappingMatch;
import com.ants.restful.request.RequestMappingBean;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 最后一个链
 *
 * @author MrShun
 * @version 1.0
 * @date 2017-11-24
 */
public class RenderHandler implements Handler {

    private final List<RequestMappingBean> requestMappingManager;

    private final Constant constant;

    public RenderHandler(Constant constant) {
        this.requestMappingManager = RequestMappingManager.getRequestMappingManager();
        this.constant = constant;
    }

    @Override
    public boolean preHandler(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestMappingBean bean = MappingMatch.match(requestMappingManager, target);
        //匹配不到请求直接放回到过滤器
        if (bean == null) {
            return true;
        } else {
            String type = request.getMethod();
            RequestMethod requestType = bean.getRequestType();
            //判断请求类型 为null时都能访问
            if (requestType == null || type.equals(String.valueOf(requestType))) {
                //绑定request,response
                ContextRequestManager.set(new ClientRequest(request, response));
                //参数绑定并且进行数据校验
                Object[] args = BindingParams.bingingValidate(target, bean, request, response);

                //绑定当前Request,Response
                Object object = bean.getObject();
                Field[] declaredFields = object.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.getType() == HttpServletRequest.class) {
                        ReflectionUtils.makeAccessible(field);
                        field.set(object, ContextRequestManager.get().getRequest());
                        continue;
                    } else if (field.getType() == HttpServletResponse.class) {
                        ReflectionUtils.makeAccessible(field);
                        field.set(object, ContextRequestManager.get().getResponse());
                        continue;
                    } else if (field.getType() == HttpSession.class) {
                        ReflectionUtils.makeAccessible(field);
                        field.set(object, ContextRequestManager.get().getSession());
                        continue;
                    }
                }
                //打印输出日志
                Log.action(target, request, bean);

                Method method = bean.getMethod();
                //取消方法类型检查提高性能
                method.setAccessible(true);

                Object data = method.invoke(bean.getProxy(), args);

                Class<?> returnType = method.getReturnType();
                //清除request,response
                ContextRequestManager.remove();
                //TODO 如果为Debug模式并且参数含有doc参数, 则生成api文档
                String doc = request.getParameter("_doc");
                if (AppConstant.DEBUG && doc != null) {
                    //TODO 文档生成操作
                }
                //有返回值的时候
                if (data != null) {
                    if (returnType == String.class) {
                        //重定向
                        if (String.valueOf(data).indexOf("redirect:") != -1) {
                            String rect = String.valueOf(data).split("redirect:")[1];
                            response.sendRedirect(request.getContextPath() + rect);
                            return true;
                        }
                        View.render(data, request, response);
                    } else if (returnType == ModelAndView.class) {
                        View.render((ModelAndView) data, request, response);
                    } else if (returnType == Resource.class) {
                        Resource res = (Resource) data;
                        res.render(request, response);
                    } else if (returnType == Object.class) {
                        Json.writeJson(data, response, true);
                    } else {
                        //返回Response响应数据, 不带序列化
                        Json.writeJson(data, response, false);
                    }
                }
            } else {
                Json.writeJson(Json.exception(ResponseCode.REQUEST_TYPE_ERROR), response);
            }
            return false;
        }
    }
}
