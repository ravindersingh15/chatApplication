package com.chat.model;

public class MessagePacket {
    private String senderId;
    private String receiverId;
    private String messageContent;

    public MessagePacket(String senderId, String receiverId, String messageContent) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageContent() {
        return messageContent;
    }
}
