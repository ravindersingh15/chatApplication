package com.chat.backend.model;

public class LoginResponse {
    private Long userId;
    private String userName;
    private String emailId;
    private String token;

    public LoginResponse(Long userId, String userName, String emailId, String token) {
        this.userId = userId;
        this.userName = userName;
        this.emailId = emailId;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getToken() {
        return token;
    }
}
