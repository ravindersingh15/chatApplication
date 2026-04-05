package com.chat.api;

import com.chat.auth.User;
import com.chat.utils.ApiCaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

public class Authentication {

    public User login(String emailId, String password) {
        try {
            Map<String, String> payloadMap = new HashMap<>();
            payloadMap.put("emailId", emailId);
            payloadMap.put("password", password);
            ObjectMapper mapper = new ObjectMapper();
            String payload = mapper.writeValueAsString(payloadMap);
            String response = ApiCaller.callApi("/auth/login", "POST", null, payload);
            if (response == null) {
                return null;
            }
            User user = mapper.readValue(response, User.class);
            if (user != null && user.getToken() != null) {
                ApiCaller.saveToken(user.getToken());
            }
            return user;
        } catch (Exception e) {
            System.err.println("Failed to parse login response: " + e.getMessage());
            return null;
        }
    }

    public User register(String emailId, String name, String password) {
        try {
            Map<String, String> payloadMap = new HashMap<>();
            payloadMap.put("emailId", emailId);
            payloadMap.put("password", password);
            payloadMap.put("userName", name);
            ObjectMapper mapper = new ObjectMapper();
            String payload = mapper.writeValueAsString(payloadMap);
            String response = ApiCaller.callApi("/auth/register", "POST", null, payload);
            if (response == null) {
                return null;
            }
            User user = mapper.readValue(response, User.class);
            if (user != null && user.getToken() != null) {
                ApiCaller.saveToken(user.getToken());
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean logout(User user) {
        ApiCaller.clearToken();
        return true;
    }
}