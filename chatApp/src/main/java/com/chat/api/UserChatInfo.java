package com.chat.api;

import java.util.List;



import com.chat.auth.User;
import com.chat.model.MessageHistory;
import com.chat.utils.ApiCaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class UserChatInfo {
    public List<User> getChatList() {
        String response = ApiCaller.callApi("/user/chat_list", "GET", null, null);

        if (response == null)
            return null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, new TypeReference<List<User>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MessageHistory> getChatHistory(String userId) {
        String baseUrl = "/messages/history";
        String path = baseUrl + "/" + userId;
        
        String response = ApiCaller.callApi(path, "GET", null, null);

        if (response == null)
            return List.of();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, new TypeReference<List<MessageHistory>>() {
            });
        } catch (Exception e) {
            System.err.println("Failed to parse chat history response: " + e.getMessage());
            return List.of();
        }
    }

    public User getUserByEmail(String email) {
        String response = ApiCaller.callApi("/user/email/" + email, "GET", null, null);

        if (response == null)
            return null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
