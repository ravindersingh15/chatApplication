package com.chat.api;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.Instant;
import java.time.Duration;

import com.chat.messageQueue.UserStatusDTO;
import org.eclipse.paho.client.mqttv3.MqttClient;

import com.chat.auth.User;
import com.chat.model.Chat;
import com.chat.model.MessagePacket;
import com.chat.utils.ApiCaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class UserChatInfo {
    private final ObjectMapper mapper = new ObjectMapper();

    public UserChatInfo(MqttClient mqttClient) {
        // mqttClient is handled at the connection level now, kept for backward compatibility if needed
    }

    public List<Chat> getChatList() {
        String response = ApiCaller.callApi("/user/chat_list", "GET", null, null);

        if (response == null) return List.of();

        try {
            return mapper.readValue(response, new TypeReference<List<Chat>>() {});
        } catch (Exception e) {
            System.err.println("Failed to parse chat list: " + e.getMessage());
            return List.of();
        }
    }

    public List<MessagePacket> getChatHistory(String chatId) {
        String path = "/messages/chat/" + chatId;
        
        String response = ApiCaller.callApi(path, "GET", null, null);

        if (response == null) return List.of();
        
        try {
            return mapper.readValue(response, new TypeReference<List<MessagePacket>>() {});
        } catch (Exception e) {
            System.err.println("Failed to parse chat history response: " + e.getMessage());
            return List.of();
        }
    }

    public Chat createDirectChat(String userId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            try {
                long uId = Long.parseLong(userId);
                payload.put("user2Id", uId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid user ID: " + userId);
                return null;
            }

            String jsonPayload = mapper.writeValueAsString(payload);
            String response = ApiCaller.callApi("/user/create_direct_chat", "POST", null, jsonPayload);
            if (response == null) return null;
            return mapper.readValue(response, Chat.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Chat createGroupChat(String chatName, List<String> userIds) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("chatName", chatName);
            List<Long> memberIds = new ArrayList<>();
            for (String idStr : userIds) {
                memberIds.add(Long.parseLong(idStr));
            }
            payload.put("memberIds", memberIds);

            String jsonPayload = mapper.writeValueAsString(payload);
            String response = ApiCaller.callApi("/user/create_group_chat", "POST", null, jsonPayload);
            if (response == null) return null;
            return mapper.readValue(response, Chat.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByEmail(String email) {
        String response = ApiCaller.callApi("/user/email/" + email, "GET", null, null);

        if (response == null) return null;

        try {
            return mapper.readValue(response, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserLastSeen(String userId) {
        String path = "/users/" + userId + "/lastSeen";
        Map<String, String> headers = new HashMap<>();
        String response = ApiCaller.callApi(path, "GET", headers, null);

        if (response != null) {
            try {
                UserStatusDTO status = mapper.readValue(response, UserStatusDTO.class);

                if (status.isOnline()) {
                    return "Online";
                }

                Timestamp lastSeen = status.getLastSeen();
                if (lastSeen != null) {

                    Instant lastSeenInstant = lastSeen.toInstant();
                    Instant now = Instant.now();
                    Duration duration = Duration.between(lastSeenInstant, now);

                    if (duration.toMinutes() < 1) {
                        return "Last seen just now";
                    } else if (duration.toHours() < 1) {
                        return "Last seen " + duration.toMinutes() + " minutes ago";
                    } else if (duration.toDays() < 1) {
                        return "Last seen " + duration.toHours() + " hours ago";
                    } else {
                        return "Last seen " + duration.toDays() + " days ago";
                    }
                }

            } catch (Exception e) {
                System.err.println("Failed to parse last seen response: " + e.getMessage());
            }
        }

        return "Unknown";
    }
}
