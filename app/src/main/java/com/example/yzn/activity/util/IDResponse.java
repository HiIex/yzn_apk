package com.example.yzn.activity.util;

public class IDResponse {
    public static final int MATCH=1;
    public static final int ERROR=0;
    private int state;
    private String id;
    private String salt;
    private String nickname;

    public IDResponse(int state,String id,String salt,String nickname){
        this.state=state;
        this.id=id;
        this.salt=salt;
        this.nickname=nickname;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}