package com.chat;

import com.chat.auth.User;
import com.chat.auth.VerifyUser;
import com.chat.chatController.ChatManager;
import com.chat.chatController.ReceiveMessage;
import com.chat.messageQueue.MqttClientManager;
import com.chat.messageQueue.MqttMessageHandler;
import com.chat.utils.ApiCaller;
import java.io.File;
import java.util.UUID;

import javax.ws.rs.client.ClientBuilder;

import org.eclipse.paho.client.mqttv3.MqttClient;

import javax.ws.rs.client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ChatClient {

    private ChatAppConfiguration config;

    public void start() {

        try {
            
            try {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                config = mapper.readValue(new File("config.yml"), ChatAppConfiguration.class);

            } catch (Exception e) {
                System.err.println("❌ Failed to load configuration: " + e.getMessage());
                e.printStackTrace();
            }

            Client client = ClientBuilder.newClient();
            String baseUrl = config.getBaseUrl();

            ApiCaller.init(client, baseUrl);

            VerifyUser verifyUser = new VerifyUser();
            User user = verifyUser.verify();

            String brokerUrl = config.getMqttConfiguration().getBrokerUrl();
            String clientId = UUID.randomUUID().toString();;
            MqttClientManager mqttManager = new MqttClientManager(brokerUrl, clientId);
            MqttClient mqttClient = mqttManager.getClient();
            ReceiveMessage receiveMessage = new ReceiveMessage();
            mqttClient.setCallback(new MqttMessageHandler(receiveMessage));

            ChatManager chatManager = new ChatManager(user, mqttClient);
            mqttManager.connect();
            chatManager.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to load configuration");
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
