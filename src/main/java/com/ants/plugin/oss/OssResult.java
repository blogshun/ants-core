package com.ants.plugin.oss;

/**
 * @author MrShun
 * @version 1.0
 */
public class OssResult {

    private Boolean isOk;

    private String message;

    private String url;

    private String eTag;

    public OssResult(Boolean isOk, String message){
        this.isOk = isOk;
        this.message = message;
    }

    public OssResult(Boolean isOk, String message, String url, String eTag) {
        this.isOk = isOk;
        this.message = message;
        this.url = url;
        this.eTag = eTag;
    }

    public Boolean isOk() {
        return isOk;
    }

    public void setOk(Boolean ok) {
        isOk = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }
}
