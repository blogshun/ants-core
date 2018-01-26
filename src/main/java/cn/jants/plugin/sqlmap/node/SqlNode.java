package cn.jants.plugin.sqlmap.node;

public interface SqlNode {

    /**
     * 根据条件得到结果
     *
     * @param obj 条件对象
     * @return
     */
    String getResult(Object obj);
}
