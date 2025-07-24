package com.chat.utils;
import com.chat.auth.User;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TextMessageDTO {
    public String senderEmail;
    public String senderName;
    public String receiverEmail;
    public String receiverName;
    public String message;
    public String senderId;
    public String receiverId;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Default constructor for deserialization
    public TextMessageDTO() {}

    // Constructor using User objects
    public TextMessageDTO(User sender, User receiver, String message) {
        this.senderEmail = sender.getEmailId();
        this.senderName = sender.getUserName();
        this.senderId = sender.getUserId();
        this.receiverEmail = receiver.getEmailId();
        this.receiverName = receiver.getUserName();
        this.receiverId = receiver.getUserId();
        this.message = message;
    }

    // Static method to serialize
    public static String serialize(TextMessageDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Static method to deserialize
    public static TextMessageDTO deserialize(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, TextMessageDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
