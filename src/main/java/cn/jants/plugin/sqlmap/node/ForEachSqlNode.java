package cn.jants.plugin.sqlmap.node;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.Map;

public class ForEachSqlNode implements SqlNode {

    private String collection;

    private String item;

    private String separator;

    private String open;

    private String close;

    private String context;

    public ForEachSqlNode(Node node) {
        Element el = (Element) node;
        this.collection = el.getAttribute("collection");
        this.item = el.getAttribute("item");
        this.separator = el.getAttribute("separator");
        this.open = el.getAttribute("open");
        this.close = el.getAttribute("close");
        this.context = el.getTextContent().trim();
    }

    private String list(Collection collection) {
        String sbx = "";
        for (Object o : collection) {
            String temp = "";
            if (o instanceof String) {
                temp = context.replace("#{" + item + "}", "'" + o + "'");
            } else {
                temp = context.replace("#{" + item + "}", String.valueOf(o));
            }
            sbx = sbx + temp + separator;
        }
        return sbx;
    }

    private String array(Object[] objs) {
        String sbx = "";
        if (objs != null) {
            for (Object obj : objs) {
                String temp = "";
                if (obj instanceof String) {
                    temp = context.replace("#{" + item + "}", "'" + obj + "'");
                } else if (obj instanceof Integer) {
                    temp = context.replace("#{" + item + "}", String.valueOf(obj));
                } else {
                    throw new RuntimeException("arrays can only store strings or integers!");
                }
                sbx = sbx + temp + separator;
            }
        }
        return sbx;
    }

    @Override
    public String getResult(Object obj) {
        StringBuffer sb = new StringBuffer(open);
        String sbx = "";
        if (obj instanceof Collection) {
            sbx = list((Collection) obj);
        } else if (obj instanceof Object[]) {
            sbx = array((Object[]) obj);
        } else if (obj instanceof Map) {
            Object result = ((Map) obj).get(collection);
            if(result == null){
                return "";
            }else{
                if (result instanceof Collection) {
                    sbx = list((Collection) result);
                } else if (result instanceof Object[]) {
                    sbx = array((Object[]) result);
                } else {
                    throw new IllegalArgumentException(collection + " can only store collections or arrays!");
                }
            }
        }
        if (!sbx.isEmpty()) {
            sbx = sbx.substring(0, sbx.length() - separator.length());
        }
        sb.append(sbx + close);
        return sb.toString();
    }
}
