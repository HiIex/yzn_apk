package com.example.yzn.activity.util;

public class LoginRequest {
    private String id;
    private String cypher;

    public LoginRequest(String id,String cypher){
        this.id=id;
        this.cypher=cypher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCypher() {
        return cypher;
    }

    public void setCypher(String cypher) {
        this.cypher = cypher;
    }
}