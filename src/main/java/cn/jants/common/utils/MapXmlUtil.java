package cn.jants.common.utils;

import cn.jants.common.bean.JsonMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 简单的xml和map相互转换
 *
 * @author MrShun
 * @version 1.0
 */
public class MapXmlUtil {

    private final static Logger LOG = LoggerFactory.getLogger(MapXmlUtil.class);

    /**
     * 将Map数据转换成xml格式
     *
     * @param map     对象数据
     * @param tagName 标签节点
     * @return
     */
    public static String map2Xml(TreeMap map, String tagName) {
        StringBuffer sb = new StringBuffer("");
        sb.append("<" + tagName + ">");
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
            sb.append("<").append(key).append(">");
            if (value instanceof String) {
                sb.append("<![CDATA[").append(value).append("]]>");
            } else {
                sb.append(value);
            }
            sb.append("</").append(key).append(">");
        }
        sb.append("</" + tagName + ">");
        return sb.toString();
    }

    /**
     * 将xml转成map
     *
     * @param strXml  xml字符串
     * @param tagName 标签节点
     * @return Map
     * @description 将xml字符串转换成map
     */
    public static Map xml2Map(String strXml, String tagName) {
        JsonMap map = JsonMap.newJsonMap();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = dbf.newDocumentBuilder();
            //将字符串转为XML
            Document doc = documentBuilder.parse(IOUtil.parseInputStream(strXml));
            // 获取根节点
            NodeList list = doc.getElementsByTagName(tagName);
            //遍历节点
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        map.put(childNode.getNodeName(), childNode.getTextContent());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("xml to map is error > " + e.getMessage());
        }
        return map;
    }
}
