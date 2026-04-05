package com.chat.backend.model;

public class AuthUser {
    private Long userId;
    private String userName;
    private String emailId;
    private String password;

    public AuthUser() {}

    public AuthUser(Long userId, String userName, String emailId, String password) {
        this.userId = userId;
        this.userName = userName;
        this.emailId = emailId;
        this.password = password;  
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
