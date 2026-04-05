package com.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Chat {
    @JsonProperty("chatId")
    private Long chatId;

    @JsonProperty("chatType")
    private String chatType;

    @JsonProperty("chatName")
    private String chatName;

    @JsonProperty("createdBy")
    private Long createdBy;

    @JsonProperty("createdAt")
    private Timestamp createdAt;

    @JsonProperty("lastMessageId")
    private Long lastMessageId;

    @JsonProperty("memberIds")
    private List<String> memberIds;

    public Chat() {}

    public String getChatId() {
        return String.valueOf(chatId);
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}
