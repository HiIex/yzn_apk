package com.example.yzn.activity.util;

public class FriendApply {
    private String fromid;
    private String toid;

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public FriendApply(String fromid, String toid) {
        this.fromid = fromid;
        this.toid = toid;
    }
}
