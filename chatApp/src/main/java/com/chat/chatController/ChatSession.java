package com.chat.chatController;

import com.chat.auth.User;
import com.chat.messageQueue.MqttPublisher;
import com.chat.messageQueue.MqttSubscriber;
import com.chat.model.Chat;
import com.chat.model.MessagePacket;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.Scanner;

public class ChatSession {
    private Scanner scanner = new Scanner(System.in);
    private User user;
    private MqttClient client;
    private MqttPublisher publisher;
    private MqttSubscriber subscriber;

    public ChatSession(User user, MqttClient mqttClient) {
        this.user = user;
        this.client = mqttClient;
        this.publisher = new MqttPublisher(this.client);
        this.subscriber = new MqttSubscriber(this.client);
    }

    public void subscribe(String topic) {
        subscriber.subscribe(topic);
    }

    public void unsubscribe(String topic) {
        subscriber.unsubscribe(topic);
    }

    public void startChat(Chat chat) {
        while(true) {
            System.out.print("[You]: ");
            String messageContent = scanner.nextLine();
            if ("/exit".equalsIgnoreCase(messageContent.trim())) {
                break;
            }
            if (messageContent.trim().isEmpty()) continue;

            MessagePacket msg = new MessagePacket(chat.getChatId(), user.getUserId(), user.getUserName(), messageContent);
            
            // Send to chat topic
            String topic = "chat/" + chat.getChatId();
            publisher.sendMessage(topic, MessagePacket.serialize(msg));
        }
    }
}
