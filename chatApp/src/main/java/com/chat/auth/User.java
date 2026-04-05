package com.chat.auth;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "emailId")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String userId;
    
    @JsonProperty("name")
    @JsonAlias("userName")
    private String userName;
    
    private String emailId;
    private boolean verified = false;
    
    private String token;

    public User(String userName, String emailId, String userId) {
        this.userName = userName;
        this.emailId = emailId;
        this.userId = userId;
        this.verified = true;
    }

    public User() {}

    public String getUserName() {
        return this.userName;
    }
    public String getUserId() {
       return userId;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerified () {
        return this.verified;
    }
}