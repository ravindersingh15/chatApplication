package com.chat.backend.queue;

import com.rabbitmq.client.*;
import com.chat.backend.dao.MessageDao;
import com.chat.backend.dao.UserChatDao;
import com.chat.backend.model.MessagePacket;
import com.chat.backend.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RabbitMQConsumerWorker implements Runnable {

    private final static String QUEUE_NAME = "chat.save.queue";
    private final Connection connection;
    private final MessageDao messageDao;
    private final UserChatDao userChatDao;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RabbitMQConsumerWorker(Connection connection, MessageDao messageDao, UserChatDao userChatDao) {
        this.connection = connection;
        this.messageDao = messageDao;
        this.userChatDao = userChatDao;
    }

    @Override
    public void run() {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            MessageService messageService = new MessageService(userChatDao, messageDao);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                executor.submit(() -> {
                    try {
                        String json = new String(delivery.getBody(), "UTF-8");
                        MessagePacket msg = objectMapper.readValue(json, MessagePacket.class);
                        messageService.saveIncommingMessages(msg);
                        System.out.println("✅ Saved to DB: " + msg.getMessageContent());
                    } catch (Exception e) {
                        System.err.println("❌ Error processing message: " + e.getMessage());
                    }
                });
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
            System.out.println("🟢 RabbitMQ Consumer started...");

        } catch (IOException e) {
            System.err.println("❌ Error starting RabbitMQ Consumer: " + e.getMessage());
        }
    }
}
