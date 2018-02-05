package cn.jants.restful.request;

import cn.jants.common.annotation.action.Param;
import cn.jants.common.annotation.action.PathVariable;
import cn.jants.common.bean.JsonMap;
import cn.jants.common.exception.TipException;
import cn.jants.common.utils.IOUtil;
import cn.jants.core.utils.EntityUtil;
import cn.jants.core.utils.ParamTypeUtil;
import cn.jants.restful.bind.LocalVariableTableParameterNameDiscoverer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
        outLoop:
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
                if (jsonBodyStr == null) {
                    JsonMap jsonMap = JsonMap.newJsonMap();
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                        String[] values = entry.getValue();
                        jsonMap.put(entry.getKey(), values.length == 1 ? values[0] : values);
                    }
                    args[i] = jsonMap;

                } else {
                    args[i] = JSON.parseObject(jsonBodyStr, JsonMap.class);
                }
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
                                continue outLoop;
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
                            continue outLoop;
                        }
                    }
                }

                //发现对象是实体对象的时候 弃用parameterType.getDeclaredAnnotation(Entity.class)
                if (parameterType.getClassLoader() != null) {
                    Object entityObj = null;
                    if(jsonBodyStr == null){
                        entityObj = parameterType.newInstance();
                        Field[] fields = parameterType.getFields();
                        for (Field field : fields) {
                            String[] fieldVal = null;
                            if (field.getType().isArray()) {
                                JSONArray jsonArray = jsonObject.getJSONArray(field.getName());
                                fieldVal = new String[jsonArray.size()];
                                for (int j = 0; j < fieldVal.length; j++) {
                                    fieldVal[j] = String.valueOf(jsonArray.get(j));
                                }
                            } else {
                                fieldVal = new String[]{jsonObject.getString(field.getName())};
                            }
                            EntityUtil.optSetMethod(field, fieldVal, entityObj, errMsgs);
                        }
                    }else {
                        entityObj = JSON.parseObject(jsonBodyStr, parameterType);
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
                            if(jsonObject != null) {
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
