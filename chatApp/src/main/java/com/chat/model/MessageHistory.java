package com.chat.model;

public class MessageHistory {
    private String senderName;
    private String receiverName;
    private String message;
    public MessageHistory() {}
    public MessageHistory(String senderName, String receiverName, String message) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getMessage() {
        return message;
    }
}
