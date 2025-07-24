package com.udbhav.sherlock.model;

public class Message {
    private String receiverName;
    private String senderName;
    private String message;

    public Message() {}
    public Message(String receiverName, String senderName, String message) {
        this.receiverName = receiverName;
        this.senderName = senderName;
        this.message = message;
    }
    public String getReceiverName() {
        return receiverName;
    }
    public String getSenderName() {
        return senderName;
    }
    public String getMessage() {
        return message;
    }
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
