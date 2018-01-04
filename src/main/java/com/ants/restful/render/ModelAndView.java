package com.ants.restful.render;

import java.io.Serializable;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-12-18
 */
public class ModelAndView implements Serializable {

    private String view;

    private Map model;

    public ModelAndView(String view){
        this.view = view;
    }

    public ModelAndView(String view, Map model){
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map getModel() {
        return model;
    }

    public void setModel(Map model) {
        this.model = model;
    }

    public ModelAndView put(String key, Object valve){
        model.put(key, valve);
        return this;
    }
}
