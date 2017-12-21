package com.ants.restful.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ants.common.annotation.action.Entity;
import com.ants.common.annotation.action.Param;
import com.ants.common.annotation.action.PathVariable;
import com.ants.common.bean.JsonMap;
import com.ants.common.exception.TipException;
import com.ants.common.utils.IOUtil;
import com.ants.core.utils.EntityUtil;
import com.ants.core.utils.ParamTypeUtil;
import com.ants.restful.bind.LocalVariableTableParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 参数绑定并且进行数据校验
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017-06-16
 */
public class BindingParams {

    private final static LocalVariableTableParameterNameDiscoverer LVP = new LocalVariableTableParameterNameDiscoverer();

    public static Object[] bingingValidate(String target, RequestMappingBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Method method = bean.getMethod();
        //迭代方法参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return null;
        }
        //定义参数数组并且绑定变量
        Object[] args = new Object[parameterTypes.length];
        //获取参数名称
        String[] params = LVP.getParameterNames(method);
        //定义错误消息
        List<String> errMsgs = new ArrayList<>();
        String jsonBodyStr = null;
        JSONObject jsonObject = null;
        if ("application/json".equals(request.getContentType())) {
            jsonBodyStr = IOUtil.parseStr(request.getInputStream());
            try {
                jsonObject = JSON.parseObject(jsonBodyStr);
                bean.setParams(jsonObject);
            } catch (Exception e) {
                throw new TipException("客户端为 application/json 格式, 参数转json失败 params > " + jsonBodyStr + "!");
            }
        }

        //给方法参数赋值
        outterLoop:
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                args[i] = request;
            } else if (parameterType == HttpServletResponse.class) {
                args[i] = response;
            } else if (parameterType == HttpSession.class) {
                args[i] = request.getSession();
            } else if (parameterType == Part.class || parameterType == Part[].class) {
                if (parameterType.isArray()) {
                    args[i] = request.getParts().toArray();
                } else {
                    args[i] = request.getPart(params[i]);
                }
            } else if (parameterType == Map.class || parameterType == HashMap.class) {
                args[i] = jsonBodyStr == null ? request.getParameterMap() : JSON.parseObject(jsonBodyStr, Map.class);
            } else if (parameterType == JsonMap.class) {
                JsonMap jsonMap = JsonMap.newJsonMap();
                Map<String, Object> parameterMap = jsonBodyStr == null ? request.getParameterMap() : JSON.parseObject(jsonBodyStr, Map.class);
                for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
                    jsonMap.put(entry.getKey(), entry.getValue());
                }
                args[i] = jsonMap;
            } else {

                //获取方法参数注解并进行校验
                Annotation[][] annotations = method.getParameterAnnotations();
                if (annotations.length > 0) {
                    for (int j = 0; j < annotations[i].length; j++) {
                        Annotation annotation = annotations[i][j];
                        //处理PathVariable注解, 将URL变量参数绑定到方法参数上面
                        if (annotation instanceof PathVariable) {
                            Map<String, String> uriValues = MappingMatch.getURIValues(bean.getCurrentUrl(), target);
                            if (uriValues != null) {
                                String pathValue = ((PathVariable) annotation).value();
                                String val = "".equals(pathValue) ? uriValues.get(params[i]) : uriValues.get(pathValue);
                                if (parameterType.isArray()) {
                                    args[i] = ParamTypeUtil.parse(new String[]{val}, parameterType);
                                } else {
                                    args[i] = ParamTypeUtil.parse(val, parameterType);
                                }
                                continue outterLoop;
                            }
                        }

                        //处理Param注解, 参数校验
                        if (annotation instanceof Param) {
                            Param param = (Param) annotation;
                            String regexType = param.type().getRegex();
                            String paramValue = (regexType != null) ? regexType : param.regex();
                            String[] parameterValues = request.getParameterValues(params[i]);
                            //Param注解
                            if (parameterValues == null || parameterValues.length == 0) {
                                if (param.msg() == null || "".equals(param.msg())) {
                                    if (regexType != null) {
                                        errMsgs.add(params[i].concat(" ").concat(param.type().getMsg()));
                                    } else {
                                        errMsgs.add("the request '" + params[i] + "' parameter cannot be missing");
                                    }
                                } else {
                                    errMsgs.add(params[i].concat(" ").concat(param.msg()));
                                }
                            } else {
                                //有正则表达式
                                if (!"".equals(paramValue)) {
                                    String msg = param.msg() == null ? "the request '" + params[i] + "'  parameter validate is not passed" : param.msg();
                                    if (parameterType.isArray()) {
                                        for (String parameterValue : parameterValues) {
                                            if (!Pattern.matches(paramValue, parameterValue)) {
                                                errMsgs.add(msg);
                                            }
                                            // throw new HandlerException(msg);
                                        }
                                        args[i] = ParamTypeUtil.parse(parameterValues, parameterType);
                                    } else {
                                        if (!Pattern.matches(paramValue, parameterValues[0])) {
                                            errMsgs.add(msg);
                                        }//throw new HandlerException(msg);
                                        else {
                                            args[i] = ParamTypeUtil.parse(parameterValues[0], parameterType);
                                        }
                                    }
                                } else {
                                    if (parameterType.isArray()) {
                                        args[i] = ParamTypeUtil.parse(parameterValues, parameterType);
                                    } else {
                                        args[i] = ParamTypeUtil.parse(parameterValues[0], parameterType);
                                    }
                                }
                            }
                            continue outterLoop;
                        }
                    }
                }

                //发现对象是实体对象的时候
                if (parameterType.getAnnotation(Entity.class) != null) {
                    Object entityObj = null;
                    try {
                        entityObj = parameterType.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    Field[] fields = parameterType.getDeclaredFields();
                    for (Field field : fields) {
                        String[] fieldVal = null;
                        if (field.getType().isArray()) {
                            JSONArray jsonArray = jsonObject.getJSONArray(field.getName());
                            String[] s = new String[jsonArray.size()];
                            for (int j = 0; j < s.length; j++) {
                                s[j] = String.valueOf(jsonArray.get(j));
                            }
                            fieldVal = jsonBodyStr == null ? request.getParameterValues(field.getName()) : s;
                        } else {
                            fieldVal = jsonBodyStr == null ? request.getParameterValues(field.getName()) :
                                    new String[]{jsonObject.getString(field.getName())};
                        }
                        EntityUtil.optSetMethod(field, fieldVal, entityObj, errMsgs);
                    }
                    args[i] = entityObj;
                } else {
                    String[] parameterValues = parameterType.isArray() ? request.getParameterValues(params[i] + "[]") : request.getParameterValues(params[i]);
                    //判断参数是否为数组
                    if (parameterValues != null) {
                        if (parameterType.isArray()) {
                            args[i] = JSON.parseObject(JSON.toJSONString(ParamTypeUtil.parse(parameterValues, parameterType)), parameterType);
                        } else {
                            args[i] = ParamTypeUtil.parse(parameterValues[0], parameterType);
                        }
                    }
                    //处理基本数据类型情况
                    else {
                        if (jsonBodyStr == null) {
                            if (parameterType.isPrimitive()) {
                                args[i] = ParamTypeUtil.setDefault(parameterType);
                            }
                        } else {
                            if (parameterType.isArray()) {
                                JSONArray jsonArray = jsonObject.getJSONArray(params[i]);
                                String[] s = new String[jsonArray.size()];
                                for (int j = 0; j < s.length; j++) {
                                    s[j] = String.valueOf(jsonArray.get(j));
                                }
                                args[i] = ParamTypeUtil.parse(s, parameterType);
                            } else {
                                args[i] = ParamTypeUtil.parse(jsonObject.getString(params[i]), parameterType);
                            }
                        }
                    }
                }
            }
        }
        //绑定错误消息
        if (parameterTypes[parameterTypes.length - 1] == BindingResult.class) {
            args[parameterTypes.length - 1] = new BindingResult(errMsgs);
        } else if (errMsgs != null && errMsgs.size() > 0) {
            throw new TipException(errMsgs.get(0));
        }
        return args;
    }
}
