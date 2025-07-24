package com.chat.messageQueue;

import org.eclipse.paho.client.mqttv3.*;

public class MqttPublisher {
    private final MqttClient client;

    public MqttPublisher(MqttClient client) {
        this.client = client;
    }

    public void sendMessage(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
