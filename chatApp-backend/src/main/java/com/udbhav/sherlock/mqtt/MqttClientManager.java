package com.udbhav.sherlock.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttClientManager {
    private final String broker;
    private final String clientId;
    private MqttClient client;

    public MqttClientManager(String clientId, String broker) {
        this.clientId = clientId;
        this.broker = broker;
        try {
            this.client = new MqttClient(this.broker, this.clientId);
        } catch (MqttException e) {
            System.err.println("Failed to create MQTT client: " + e.getMessage());
            // It's good practice to also print the stack trace for debugging
            e.printStackTrace();
        }
    }

    // --- THIS IS THE FIX ---
    // The method now throws MqttException so the calling code knows it failed.
    public void connect() throws MqttException {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false); // Consider true if you don't need persistent sessions
            System.out.println("Attempting to connect to MQTT broker at " + this.broker + "...");
            client.connect(options);
            System.out.println("✅ Successfully connected to MQTT broker.");
        } catch (MqttException e) {
            System.err.println("❌ Error connecting to MQTT broker. Full error details below.");
            // Print the full error stack trace to the console for detailed debugging
            e.printStackTrace();
            // Re-throw the exception so the main application can handle it
            throw e;
        }
    }
    // --- END OF FIX ---

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            System.err.println("❌ Error disconnecting from MQTT broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public MqttClient getClient() {
        return client;
    }
}
