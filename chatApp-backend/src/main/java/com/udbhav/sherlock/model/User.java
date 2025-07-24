package com.udbhav.sherlock.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
    private String userId;
    private String userName;
    private String emailId;

    public User() {}

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

}
