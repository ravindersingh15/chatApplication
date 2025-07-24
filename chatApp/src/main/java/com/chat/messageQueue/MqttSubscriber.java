package com.chat.messageQueue;

import org.eclipse.paho.client.mqttv3.*;

public class MqttSubscriber {
    private final MqttClient client;

    public MqttSubscriber(MqttClient client) {
        this.client = client;
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
