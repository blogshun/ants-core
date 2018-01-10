package cn.jants.restful.render;

import cn.jants.common.bean.Page;
import cn.jants.common.enums.ResponseCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Json输出工具
 *
 * @author MrShun
 * @version 1.0
 */
public class Json {

    /**
     * 执行状态
     */
    private static final String STATE = "code";

    /**
     * 消息描述
     */
    private static final String MSG = "message";

    /**
     * 数据对象
     */
    private static final String RESULT = "data";

    /**
     * 时间戳
     */
    private static final String TIMESPAN = "time";

    /**
     * 异常消息
     */
    private static final String ERROR = "exception";

    /**
     * 当前页数
     */
    private static final String INDEX = "index";

    /**
     * 每页大小
     */
    private static final String SIZE = "size";

    /**
     * 总记录数
     */
    private static final String TOTAL = "total";

    /**
     * 总页数
     */
    private static final String PAGES = "pages";

    /**
     * 操作成功
     *
     * @param data      数据对象
     * @param startTime 开始时间
     * @param <T>
     * @return
     */
    public static <T> Map success(T data, Long startTime) {
        if (data == null) {
            return fail(ResponseCode.DATA_NULL_ERROR);
        }
        Map map = new HashMap(10);
        map.put(STATE, ResponseCode.SUCCESS.getCode());
        map.put(MSG, ResponseCode.SUCCESS.getMsg());
        if (startTime != null) {
            map.put(TIMESPAN, System.currentTimeMillis() - startTime + " msec");
        } else if (data.getClass() == Page.class) {
            Page page = ((Page) data);
            map.put(RESULT, page.getData());
            map.put(INDEX, page.getIndex());
            map.put(SIZE, page.getSize());
            map.put(TOTAL, page.getTotal());
            map.put(PAGES, page.getPages());
        } else {
            map.put(RESULT, data);
        }
        return map;
    }

    public static <T> Map success(T data) {
        return success(data, null);
    }


    /**
     * 为了适配ui框架
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Map ui(T data) {
        if (data == null) {
            return fail(ResponseCode.DATA_NULL_ERROR);
        }
        Map map = new HashMap(10);
        map.put(STATE, ResponseCode.SUCCESS.getCode());
        map.put(MSG, ResponseCode.SUCCESS.getMsg());
        if (data.getClass() == Page.class) {
            Page page = ((Page) data);
            map.put("data", page.getData());
            map.put("total", page.getTotal());
        } else {
            map.put(RESULT, data);
        }
        return map;
    }

    /**
     * 操作异常失败
     *
     * @param responseCode 枚举响应码
     * @param exception    异常信息
     * @return
     */
    public static Object exception(ResponseCode responseCode, String exception) {
        Map map = new HashMap(10);
        map.put(STATE, responseCode.getCode());
        map.put(MSG, exception);
        map.put(ERROR, responseCode.getMsg());
        return JSON.toJSON(map);
    }

    public static Object exception(ResponseCode responseCode) {
        Map map = new HashMap(10);
        map.put(STATE, responseCode.getCode());
        map.put(MSG, responseCode.getMsg());
        return JSON.toJSON(map);
    }

    /**
     * 操作错误
     *
     * @param responseCode 枚举响应码
     * @return
     */
    public static Map fail(ResponseCode responseCode) {
        Map map = new HashMap(10);
        map.put(STATE, responseCode.getCode());
        map.put(MSG, responseCode.getMsg());
        return map;
    }

    /**
     * 自定义响应吗
     *
     * @param code 响应吗
     * @param msg  消息
     * @return
     */
    public static Map fail(int code, String msg) {
        Map map = new HashMap(10);
        map.put(STATE, code);
        map.put(MSG, msg);
        return map;
    }

    /**
     * 操作错误自定义错误码
     *
     * @param msg 自定义系统错误消息
     * @return
     */
    public static Map fail(String msg) {
        Map map = new HashMap(10);
        map.put(STATE, ResponseCode.REQUEST_ERROR.getCode());
        map.put(MSG, msg);
        return map;
    }

    /**
     * 基于PrintWriter返回json数据
     * 可以配置fastjson输出配置
     * http://blog.csdn.net/u010246789/article/details/52539576
     * 常用 SerializerFeature
     * PrettyFormat 格式化输出json
     * WriteNullStringAsEmpty   字符类型字段如果为null,输出为”“,而非null
     * WriteNullBooleanAsFalse	Boolean字段如果为null,输出为false,而非null
     * WriteNullNumberAsZero	数值字段如果为null,输出为0,而非null
     * WriteNullListAsEmpty	List字段如果为null,输出为[],而非null
     * WriteMapNullValue	是否输出值为null的字段,默认为false
     * QuoteFieldNames	输出key时是否使用双引号,默认为true
     *
     * @param data
     * @param response
     */
    public static void writeJson(Object data, HttpServletResponse response, boolean serializers) {
        response.setContentType("application/json;charset=" + response.getCharacterEncoding());
        try {
            PrintWriter w = response.getWriter();
            //返回Response响应数据
            if (serializers) {
                w.print(JSON.toJSONString(data, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullListAsEmpty));
            } else {
                w.print(JSON.toJSONString(data));
            }
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeJson(Object data, HttpServletResponse response) {
        writeJson(data, response, false);
    }
}
