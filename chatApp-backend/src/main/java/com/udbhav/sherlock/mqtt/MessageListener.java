package com.udbhav.sherlock.mqtt;

public interface MessageListener {
    void onMessageReceived(String topic, String message);
}
