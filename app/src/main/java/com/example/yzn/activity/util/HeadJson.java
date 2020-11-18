package com.example.yzn.activity.util;

public class HeadJson {

    private String id;
    private String base64Str;

    public HeadJson(String id,String base64Str){
        this.id=id;
        this.base64Str=base64Str;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBase64Str() {
        return base64Str;
    }

    public void setBase64Str(String base64Str) {
        this.base64Str = base64Str;
    }
}