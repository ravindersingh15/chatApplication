package com.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagePacket {
    @JsonProperty("message_id")
    @JsonAlias({"messageId", "id"})
    private String messageId;

    @JsonProperty("chat_id")
    @JsonAlias("chatId")
    private String chatId;

    @JsonProperty("sender_id")
    @JsonAlias("senderId")
    private String senderId;

    @JsonProperty("sender_user_name")
    @JsonAlias({"senderUserName", "senderName"})
    private String senderUserName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("timestamp")
    private long timestamp;

    public MessagePacket() {}

    public MessagePacket(String chatId, String senderId, String senderUserName, String content) {
        this.messageId = UUID.randomUUID().toString();
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderUserName = senderUserName;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderUserName() { return senderUserName; }
    public void setSenderUserName(String senderUserName) { this.senderUserName = senderUserName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Serialization and deserialization utilities
    public static String serialize(MessagePacket packet) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(packet);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MessagePacket deserialize(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, MessagePacket.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
