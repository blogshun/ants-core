package cn.jants.common.bean;

import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 格式化Json数据并输出
 *
 * @author MrShun
 * @version 1.0
 */
public class JsonFormat {

    /**
     * 数据对象
     */
    private Object data;

    /**
     * fastJson格式化参数
     */
    private SerializerFeature[] features;

    public JsonFormat(Object data) {
        this.data = data;
        this.features = new SerializerFeature[]{SerializerFeature.PrettyFormat};
    }

    public JsonFormat(Object data, SerializerFeature... features) {
        this.data = data;
        this.features = features;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public SerializerFeature[] getFeatures() {
        return features;
    }

    public void setFeatures(SerializerFeature[] features) {
        this.features = features;
    }
}
