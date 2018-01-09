package com.ants.plugin.sqlmap;

/**
 * @author MrShun
 * @version 1.0
 */
public class TextSqlNode implements SqlNode {

    private String context;

    public TextSqlNode(String context) {
        this.context = context;
    }


    @Override
    public String getResult(Object obj) {
        return context;
    }
}
