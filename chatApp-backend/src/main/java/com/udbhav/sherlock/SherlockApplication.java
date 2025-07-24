package com.udbhav.sherlock;

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
import com.udbhav.sherlock.queue.MessageQueueConfiguration;
import com.udbhav.sherlock.queue.MessageQueuePublisher;
import com.udbhav.sherlock.queue.RabbitMQConsumerWorker;
import com.udbhav.sherlock.dao.AuthUserDao;
import com.udbhav.sherlock.dao.MessageDao;
import com.udbhav.sherlock.dao.UserChatDao;
import com.udbhav.sherlock.dao.UserDao;
import com.udbhav.sherlock.mapper.AuthUserMapper;
import com.udbhav.sherlock.mapper.MessageMapper;
import com.udbhav.sherlock.mapper.UserMapper;
import com.udbhav.sherlock.mqtt.ChatMessageListener;
import com.udbhav.sherlock.mqtt.MqttClientManager;
import com.udbhav.sherlock.mqtt.MqttConfiguration;
import com.udbhav.sherlock.mqtt.MqttMessageHandler;
import com.udbhav.sherlock.mqtt.MqttSubscriber;
import com.udbhav.sherlock.resources.AuthResource;
import com.udbhav.sherlock.resources.MessageResource;
import com.udbhav.sherlock.resources.UserChatHistoryResource;

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
            jdbi.registerRowMapper(new AuthUserMapper());
            System.out.println("✅ Database connection established");

            final UserDao userDao = jdbi.onDemand(UserDao.class);
            final AuthUserDao authUserDao = jdbi.onDemand(AuthUserDao.class);
            final UserChatDao userChatDao = jdbi.onDemand(UserChatDao.class);
            final MessageDao messageDao = jdbi.onDemand(MessageDao.class);
            environment.jersey().register(new AuthResource(authUserDao));
            environment.jersey().register(new UserChatHistoryResource(userChatDao, userDao));
            environment.jersey().register(new MessageResource(messageDao));
            System.out.println("✅ Jersey resources registered");

            // --- RabbitMQ Setup ---
            MessageQueueConfiguration rmqConfig = configuration.getMessageQueueConfiguration();
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.useSslProtocol();
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
                client.setCallback(new MqttMessageHandler(new ChatMessageListener(messageDao, userChatDao, publisher)));
                MqttSubscriber subscriber = new MqttSubscriber(client);
                subscriber.subscribe("saveMessages");
                System.out.println("✅ MQTT client connected and subscribed to topic: saveMessages");
            
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
