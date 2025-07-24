package com.chat;

import com.chat.messageQueue.MqttConfiguration; 
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ChatAppConfiguration extends Configuration {
    @JsonProperty
    private String baseUrl;

    @Valid
    @NotNull
    @JsonProperty("mqtt")
    private MqttConfiguration mqttConfiguration = new MqttConfiguration();

    public String getBaseUrl() {
        return baseUrl;
    }

    public MqttConfiguration getMqttConfiguration() {
        return mqttConfiguration;
    }
}