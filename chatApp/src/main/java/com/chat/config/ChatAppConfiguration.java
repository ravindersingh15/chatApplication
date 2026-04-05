package com.chat.config;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ChatAppConfiguration extends Configuration {
    @JsonProperty
    private String baseUrl;

    @JsonProperty
    private String hashSalt;

    @Valid
    @NotNull
    @JsonProperty("mqtt")
    private MqttConfiguration mqttConfiguration = new MqttConfiguration();

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getHashSalt() {
        return hashSalt;
    }

    public MqttConfiguration getMqttConfiguration() {
        return mqttConfiguration;
    }
}