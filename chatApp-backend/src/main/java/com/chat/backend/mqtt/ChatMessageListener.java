package com.chat.backend.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chat.backend.dao.MessageDao;
import com.chat.backend.dao.UserChatDao;
import com.chat.backend.model.MessagePacket;
import com.chat.backend.queue.MessageQueuePublisher;
import com.chat.backend.service.MessageService;

public class ChatMessageListener implements MessageListener {


    MessageService messageService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MessageQueuePublisher publisher;

    public ChatMessageListener(MessageDao messageDao, UserChatDao userChatDao, MessageQueuePublisher publisher){
        this.messageService = new MessageService(userChatDao, messageDao);
        this.publisher = publisher;
    }

    @Override
    public void onMessageReceived(String topic, String message, boolean isRetained){
        System.out.println("📩 Message received on topic " + topic + ": " + message);
        try {
            MessagePacket chatMessage = mapper.readValue(message, MessagePacket.class);
            publisher.publishMessage(chatMessage);
        } catch (Exception e) {
            System.err.println("❌ Error while processing MQTT message: " + e.getMessage());
        }
    }

}
