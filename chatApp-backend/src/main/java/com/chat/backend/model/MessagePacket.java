package com.chat.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagePacket {
    
    @JsonProperty("sender_id")
    private Long senderId;
    
    @JsonProperty("chat_id")
    private Long chatId;
    
    @JsonProperty("content")
    private String messageContent;

    public MessagePacket() {}
    public MessagePacket(Long senderId, Long chatId, String messageContent) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.messageContent = messageContent;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
