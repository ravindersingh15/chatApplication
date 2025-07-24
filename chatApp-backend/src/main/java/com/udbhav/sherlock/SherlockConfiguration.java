package com.udbhav.sherlock;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.udbhav.sherlock.mqtt.MqttConfiguration;
import com.udbhav.sherlock.queue.MessageQueueConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SherlockConfiguration extends Configuration {
    
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @Valid
    @NotNull
    private MessageQueueConfiguration messageQueue = new MessageQueueConfiguration();

    @JsonProperty("rabbitmq")
    public MessageQueueConfiguration getMessageQueueConfiguration() {
        return messageQueue;
    }

    @JsonProperty("rabbitmq")
    public void setMessageQueueConfiguration(MessageQueueConfiguration messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Valid
    @NotNull
    private MqttConfiguration mqtt = new MqttConfiguration();
    @JsonProperty("mqtt")
    public MqttConfiguration getMqttConfiguration() {
        return mqtt;
    }

    @JsonProperty("mqtt")
    public void setMqttConfiguration(MqttConfiguration mqtt) {
        this.mqtt = mqtt;
    }
}