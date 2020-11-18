package com.example.yzn.activity.util;

public class OriginalImage {
    private String base64Str;

    public OriginalImage(String base64Str){
        this.base64Str=base64Str;
    }

    public String getBase64Str() {
        return base64Str;
    }

    public void setBase64Str(String base64Str) {
        this.base64Str = base64Str;
    }
}