package com.example.yzn.activity.util;

public class User {
    private String id;
    private String phone;
    private String salt;
    private String cypher;
    private String nickname;

    public User(String id,String phone,String salt,String cypher,String nickname){
        this.id=id;
        this.phone=phone;
        this.salt=salt;
        this.cypher=cypher;
        this.nickname=nickname;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCypher() {
        return cypher;
    }

    public void setCypher(String cypher) {
        this.cypher = cypher;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
