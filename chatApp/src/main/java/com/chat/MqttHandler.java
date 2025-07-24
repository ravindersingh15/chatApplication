package com.chat;

import org.eclipse.paho.client.mqttv3.*;

public class MqttHandler {
    private final String broker = "tcp://localhost:1883"; // Change if needed
    private final String topicPrefix = "user/";
    private final String username;
    private MqttClient client;

    public MqttHandler(String username) {
        this.username = username;
        try {
            this.client = new MqttClient(broker, username);
            this.client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost!");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("\n📩 Message from " + topic.replace(username, "") + ": " + message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("(sent)");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
            client.subscribe(topicPrefix + username);
            System.out.println("Connected and subscribed to your topic: " + topicPrefix + username);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String recipient, String message) {
        String topic = topicPrefix + recipient;
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
