package com.chat.api;

import com.chat.auth.User;
import com.chat.utils.ApiCaller;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Authentication {

    public User login(String emailId, String password) {
        String payload = String.format("{\"emailId\":\"%s\", \"password\":\"%s\"}", emailId, password);
        String response = ApiCaller.callApi("/auth/login", "POST", null, payload);
        if (response == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, User.class);
        } catch (Exception e) {
            System.err.println("Failed to parse login response: " + e.getMessage());
            return null;
        }
    }

    
    public User register(String emailId, String userName, String password) {
        String payload = String.format("{\"emailId\":\"%s\", \"password\":\"%s\", \"userName\":\"%s\"}",
                emailId, password, userName);
        String response = ApiCaller.callApi("/auth/register", "POST", null, payload);
        if (response == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean logout(User user) {
        // Implement logout logic
        return true;
    }
}
