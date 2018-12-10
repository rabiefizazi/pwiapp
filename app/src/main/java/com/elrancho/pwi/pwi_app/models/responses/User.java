package com.elrancho.pwi.pwi_app.models.responses;

public class User {

    private String token, userid, username, storeId;

    public User(String token, String userid, String username, String storeId) {
        this.token = token;
        this.userid = userid;
        this.username = username;
        this.storeId = storeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String Token) {
        this.token = Token;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
