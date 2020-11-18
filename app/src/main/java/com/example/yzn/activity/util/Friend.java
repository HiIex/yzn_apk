package com.example.yzn.activity.util;

public class Friend {
    private String id;
    private String nickname;
    private String remark;
    private String time;

    public Friend(){}

    public Friend(String id, String nickname, String remark, String time) {
        this.id = id;
        this.nickname = nickname;
        this.remark = remark;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
