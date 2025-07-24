package com.chat.chatController;
import com.chat.auth.User;
import com.chat.messageQueue.*;
import com.chat.model.MessagePacket;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;
import org.eclipse.paho.client.mqttv3.*;

public class ChatSession{
    Scanner scanner = new Scanner(System.in);
    private User user;
    private User receiverUser;
    private MqttClientManager clientManager;
    private MqttClient client;
    private MqttPublisher publisher;
    private final MqttSubscriber subscriber;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    

    public ChatSession(User user, MqttClient mqttClient) {
        this.user = user;
        this.client = mqttClient;
        
        this.publisher = new MqttPublisher(this.client);
        this.subscriber = new MqttSubscriber(this.client);
    }

    public void setReceiverUser(User user) {
        this.receiverUser = user;
    }

    public void subscribeToGeneralChat(String topic) {
        subscriber.subscribe(topic);
    }
    public void start() {
        while(true) {
            String messageContent = scanner.nextLine();
            sendMessage(new TextMessage(user, receiverUser, messageContent));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("❌ Error in sending message: " + e.getMessage());
            }
        }
    }


    public void sendMessage(IMessage message) {
        if(receiverUser != null) {
            message.showSenderMessage();
            String topic = "user/" + receiverUser.getUserId() + '/' + user.getUserId();
            publisher.sendMessage(topic, message.getSerializedContent());
            this.saveChatMessage(message.getMessage());
        } else{
            System.out.println("❌ No receiver user set. Please set a receiver user before sending a message.");
        }
    }

    public void stop() {
        clientManager.disconnect();
    }



    private void saveChatMessage(String message) {
        MessagePacket messagePacket = new MessagePacket(user.getUserId(), receiverUser.getUserId(), message);
        String topic = "saveMessages";
        try {
            String messageToSent = objectMapper.writeValueAsString(messagePacket);
            publisher.sendMessage(topic, messageToSent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




