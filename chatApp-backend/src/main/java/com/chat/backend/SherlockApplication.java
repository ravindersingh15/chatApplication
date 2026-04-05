package com.chat.backend;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.jdbi3.JdbiFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.jdbi.v3.core.Jdbi;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.chat.backend.queue.MessageQueueConfiguration;
import com.chat.backend.queue.MessageQueuePublisher;
import com.chat.backend.queue.RabbitMQConsumerWorker;
import com.chat.backend.dao.MessageDao;
import com.chat.backend.dao.UserChatDao;
import com.chat.backend.dao.UserDao;
import com.chat.backend.mapper.MessageMapper;
import com.chat.backend.mapper.UserMapper;
import com.chat.backend.mapper.ChatMapper;
import com.chat.backend.mqtt.ChatMessageListener;
import com.chat.backend.mqtt.MqttClientManager;
import com.chat.backend.mqtt.MqttConfiguration;
import com.chat.backend.mqtt.MqttMessageHandler;
import com.chat.backend.mqtt.MqttSubscriber;
import com.chat.backend.mqtt.MessageListener;
import com.chat.backend.mqtt.PresenceMessageListener;
import com.chat.backend.resources.AuthResource;
import com.chat.backend.resources.MessageResource;
import com.chat.backend.resources.UserChatResource;
import com.chat.backend.resources.UserResource;

public class SherlockApplication extends Application<SherlockConfiguration> {

    public static void main(final String[] args) throws Exception {
        new SherlockApplication().run(args);
    }

    @Override
    public String getName() {
        return "sherlock";
    }

    @Override
    public void initialize(final Bootstrap<SherlockConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                                               new EnvironmentVariableSubstitutor(true)
                )
        );
    }

    @Override
    public void run(SherlockConfiguration configuration, Environment environment) throws Exception {
        try {
            // --- Database Setup ---
            final JdbiFactory factory = new JdbiFactory();
            final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
            jdbi.registerRowMapper(new UserMapper());
            jdbi.registerRowMapper(new MessageMapper());
            jdbi.registerRowMapper(new ChatMapper());
            System.out.println("✅ Database connection established");

            final UserDao userDao = jdbi.onDemand(UserDao.class);
            final UserChatDao userChatDao = jdbi.onDemand(UserChatDao.class);
            final MessageDao messageDao = jdbi.onDemand(MessageDao.class);
            
            environment.jersey().register(new AuthResource(userDao));
            environment.jersey().register(new UserChatResource(userChatDao, userDao));
            environment.jersey().register(new MessageResource(messageDao, userChatDao));
            environment.jersey().register(new UserResource(userDao));
            System.out.println("✅ Jersey resources registered");

            // --- RabbitMQ Setup ---
            MessageQueueConfiguration rmqConfig = configuration.getMessageQueueConfiguration();
            ConnectionFactory connectionFactory = new ConnectionFactory();
            // connectionFactory.useSslProtocol(); // Commented out to prevent SSL handshake on non-SSL port 5672
            connectionFactory.setHost(rmqConfig.getHost());
            connectionFactory.setUsername(rmqConfig.getUsername());
            connectionFactory.setPassword(rmqConfig.getPassword());
            connectionFactory.setPort(Integer.parseInt(rmqConfig.getPort())); 

            Connection connection = connectionFactory.newConnection();
            MessageQueuePublisher publisher = new MessageQueuePublisher(connection);
            System.out.println("✅ RabbitMQ connection established");
            try {
                MqttConfiguration mqttConfig = configuration.getMqttConfiguration();
                MqttClientManager mqttManager = new MqttClientManager(mqttConfig.getClientId(), mqttConfig.getBrokerUrl());
                mqttManager.connect();

                MqttClient client = mqttManager.getClient();
                
                final MessageListener chatListener = new ChatMessageListener(messageDao, userChatDao, publisher);
                final MessageListener presenceListener = new PresenceMessageListener(userDao);
                
                client.setCallback(new MqttMessageHandler((topic, message, isRetained) -> {
                    if (topic.startsWith("chat/")) {
                        chatListener.onMessageReceived(topic, message, isRetained);
                    } else if (topic.startsWith("presence/")) {
                        presenceListener.onMessageReceived(topic, message, isRetained);
                    }
                }));
                MqttSubscriber subscriber = new MqttSubscriber(client);
                
                // Subscribe to all chat topics
                subscriber.subscribe("chat/#");
                subscriber.subscribe("presence/#");
                System.out.println("✅ MQTT client connected and subscribed to topics: chat/#, presence/#");
            
                new Thread(new RabbitMQConsumerWorker(connection, messageDao, userChatDao)).start();
                System.out.println("🟢 RabbitMQ Consumer worker started");
            } catch (Exception e) {
                System.err.println("❌ Failed to setup MQTT client. The application will not listen for MQTT messages.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("❌ Critical exception during app startup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
