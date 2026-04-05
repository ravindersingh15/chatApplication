package com.chat.messageQueue;

import com.chat.api.PresenceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.sql.Timestamp;

public class MqttClientManager {
    private final String broker;
    private final String clientId;
    private MqttClient client;

    public MqttClientManager(String brokerUrl, String clientId) {
        this.broker = brokerUrl;
        this.clientId = clientId;
        try {
            // Using MemoryPersistence to avoid FileLock reflective access warnings
            this.client = new MqttClient(this.broker, this.clientId, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connect(String userId) {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            String topic = "presence/" + userId + "/" + clientId;
            ObjectMapper mapper = new ObjectMapper();

            try {
                PresenceStatus offlineStatus = new PresenceStatus(false, new Timestamp(System.currentTimeMillis()));

                String offlinePayload = mapper.writeValueAsString(offlineStatus);
                options.setWill(topic, offlinePayload.getBytes(), 1, true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            client.connect(options);

            try {
                PresenceStatus onlineStatus = new PresenceStatus(true, new Timestamp(System.currentTimeMillis()));

                String onlinePayload = mapper.writeValueAsString(onlineStatus);
                client.publish(topic, onlinePayload.getBytes(), 1, true);

            } catch (Exception e) {
                e.printStackTrace();
            }

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