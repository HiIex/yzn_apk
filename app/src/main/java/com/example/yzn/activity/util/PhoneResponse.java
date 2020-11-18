package com.example.yzn.activity.util;

public class PhoneResponse {
    public final static int PERMIT=1;
    public final static int EXIST=0;
    private int state;

    public PhoneResponse(){}

    public PhoneResponse(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}