package com.example.yzn.activity.util;

public class AccountInfo {
    private String id;
    private boolean sex;
    private String nickname;
    private String province;
    private String city;
    private String birthday;
    private String company;
    private String position;

    public AccountInfo(String id, boolean sex, String nickname, String province, String city, String birthday, String company, String position) {
        this.id = id;
        this.sex = sex;
        this.nickname = nickname;
        this.province = province;
        this.city = city;
        this.birthday = birthday;
        this.company = company;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
