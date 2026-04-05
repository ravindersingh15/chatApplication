package com.chat;

import com.chat.auth.User;
import com.chat.auth.VerifyUser;
import com.chat.chatController.ChatManager;
import com.chat.config.ChatAppConfiguration;
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
            User user = verifyUser.verify(config.getHashSalt());

            String brokerUrl = config.getMqttConfiguration().getBrokerUrl();
            String clientId = UUID.randomUUID().toString();;
            MqttClientManager mqttManager = new MqttClientManager(brokerUrl, clientId);
            MqttClient mqttClient = mqttManager.getClient();

            ChatManager chatManager = new ChatManager(user, mqttClient);
            mqttClient.setCallback(new MqttMessageHandler(chatManager));

            mqttManager.connect(String.valueOf(user.getUserId()));
            chatManager.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to load configuration");
        }
    }

    /**
     * Main Function
     * Flow starts from here
     */
    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
