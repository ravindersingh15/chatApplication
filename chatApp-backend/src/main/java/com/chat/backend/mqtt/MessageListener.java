package com.chat.backend.mqtt;

public interface MessageListener {
    void onMessageReceived(String topic, String message, boolean isRetained);
}
