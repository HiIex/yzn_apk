package com.example.yzn.activity.util;

import android.graphics.Bitmap;

public class Account {
    private Bitmap bitmap;
    private String nickname;
    private String phone;
    private boolean isOnline;

    public Account(Bitmap bitmap,String nickname,String phone,boolean isOnline){
        this.bitmap=bitmap;
        this.nickname=nickname;
        this.phone=phone;
        this.isOnline=isOnline;
    }

    public String getNickname(){
        return this.nickname;
    }

    public String getPhone(){
        return this.phone;
    }

    public boolean getIsOnline(){
        return this.isOnline;
    }

    public Bitmap getBitmap(){
        return this.bitmap;
    }



}
