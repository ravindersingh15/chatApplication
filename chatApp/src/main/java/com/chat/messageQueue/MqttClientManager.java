package com.chat.messageQueue;
import org.eclipse.paho.client.mqttv3.*;

public class MqttClientManager {
    // 1. The hardcoded broker URL is removed from here.
    private final String broker; 
    private final String clientId;
    private MqttClient client;

    // 2. The constructor now accepts the broker URL as an argument.
    public MqttClientManager(String brokerUrl, String clientId) {
        this.broker = brokerUrl;
        this.clientId = clientId;
        try {
            // It uses the brokerUrl argument to create the client.
            this.client = new MqttClient(this.broker, this.clientId);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (client.isConnected()) client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public MqttClient getClient() {
        return client;
    }
}