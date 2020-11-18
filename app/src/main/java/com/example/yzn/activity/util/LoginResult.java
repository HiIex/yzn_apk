package com.example.yzn.activity.util;

public class LoginResult {
    public static final int ERROR=0;
    public static final int SUCCESS=1;
    public static final int FAIL=2;
    private int state;

    public LoginResult(int state){
        this.state=state;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}