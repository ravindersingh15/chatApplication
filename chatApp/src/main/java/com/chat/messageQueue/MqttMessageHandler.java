package com.chat.messageQueue;

import org.eclipse.paho.client.mqttv3.*;

public class MqttMessageHandler implements MqttCallback {

    private final MessageListener listener;

    public MqttMessageHandler(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("🔌 Connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // Notify ChatSession through the listener
        listener.onMessageReceived(topic, message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
