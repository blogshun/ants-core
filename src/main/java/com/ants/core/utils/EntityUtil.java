package com.ants.core.utils;


import com.alibaba.fastjson.annotation.JSONField;
import com.ants.common.annotation.action.Param;
import com.ants.common.utils.StrUtil;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-05-06
 */
public class EntityUtil {

    /**
     * @param field 实体里面的字段
     * @param value 数据值
     * @param obj   实体对象
     * @param <T>
     */
    public static <T> void optSetMethod(Field field, String[] value, T obj, List<String> errMsgs) {
        Param param = field.getDeclaredAnnotation(Param.class);
        if (value != null) {
            if (param != null) {
                String regexType = param.type().getRegex();
                String paramValue = (regexType != null)? regexType : param.regex();
                if (!paramValue.isEmpty()) {
                    String msg = param.msg() == null || "".equals(param.msg()) ?
                            (regexType != null ? field.getName().concat(" ").concat(param.type().getMsg()) : "the request '" +
                                    field.getName() + "'  parameter validate is not passed")
                            : field.getName().concat(" ").concat(param.msg());
                    if (field.getType().isArray()) {
                        for (String parameterValue : value) {
                            if (!Pattern.matches(paramValue, parameterValue)) {
                                errMsgs.add(msg);
                                // throw new HandlerException(msg);
                            }
                        }
                    } else {
                        if (!Pattern.matches(paramValue, value[0])) {
                            errMsgs.add(msg);
                            // throw new HandlerException(msg);
                        }
                    }
                }

            }

            try {
                field.setAccessible(true);
                if (value != null && !"".equals(value[0]) && field.getType() == Date.class) {
                    String parseStr = "yyyy-MM-dd HH:mm";
                    JSONField jsonfield = field.getAnnotation(JSONField.class);
                    if (jsonfield != null && StrUtil.notBlank(jsonfield.format())) {
                        parseStr = jsonfield.format();
                    }
                    DateFormat df = new SimpleDateFormat(parseStr);
                    field.set(obj, df.parse(value[0]));
                } else {
                    if (field.getType().isArray()) {
                        field.set(obj, ParamTypeUtil.parse(value, field.getType()));
                    } else {
                        field.set(obj, ParamTypeUtil.parse(value[0], field.getType()));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("时间参数转换异常, 类型不匹配!");
            }
        } else {
            if (param != null) {
                errMsgs.add("the request '" + field.getName() + "' parameter cannot be missing");
                // throw new HandlerException("the request '" + field.getName() + "' parameter cannot be missing");
            }
        }
    }

}
