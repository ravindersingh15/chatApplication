package com.udbhav.sherlock.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udbhav.sherlock.dao.MessageDao;
import com.udbhav.sherlock.dao.UserChatDao;
import com.udbhav.sherlock.model.Message;
import com.udbhav.sherlock.model.MessagePacket;
import com.udbhav.sherlock.queue.MessageQueuePublisher;
import com.udbhav.sherlock.service.MessageService;

public class ChatMessageListener implements MessageListener {


    MessageService messageService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MessageQueuePublisher publisher;

    public ChatMessageListener(MessageDao messageDao, UserChatDao userChatDao, MessageQueuePublisher publisher){
        this.messageService = new MessageService(userChatDao, messageDao);
        this.publisher = publisher;
    }

    @Override
    public void onMessageReceived(String topic, String message){
        System.out.println("📩 Message received on topic " + topic + ": " + message);
        try {
            MessagePacket chatMessage = mapper.readValue(message, MessagePacket.class);
            publisher.publishMessage(chatMessage);
        } catch (Exception e) {
            System.err.println("❌ Error while processing MQTT message: " + e.getMessage());
        }
    }

}
