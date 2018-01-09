package com.ants.restful.request;

import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class BindingResult {

    private List<String> errMsgs;

    public BindingResult(List<String> errMsgs){
        this.errMsgs = errMsgs;
    }

    public boolean validate(){
        if(errMsgs != null && errMsgs.size() > 0) {
            return false;
        }
        return true;
    }

    public List<String> getErrorMsgs(){
        return errMsgs;
    }

}
