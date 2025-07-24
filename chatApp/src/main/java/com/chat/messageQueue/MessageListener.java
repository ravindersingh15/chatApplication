package com.chat.messageQueue;

public interface MessageListener {
    void onMessageReceived(String topic, String message);
}
