package com.udbhav.sherlock.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.udbhav.sherlock.model.MessagePacket;

public class MessageQueuePublisher {
    private static final String EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY = "chat.save.queue";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Channel channel;

    public MessageQueuePublisher(Connection connection) throws Exception {
        this.channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
        channel.queueDeclare(ROUTING_KEY, true, false, false, null);
        channel.queueBind(ROUTING_KEY, EXCHANGE_NAME, ROUTING_KEY);
    }

    public void publishMessage(MessagePacket message) throws Exception {
        String json = objectMapper.writeValueAsString(message);
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, json.getBytes("UTF-8"));
        System.out.println("📤 Sent to RabbitMQ: " + json);
    }

    public void close() throws Exception {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }
}
