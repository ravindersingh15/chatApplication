package com.chat.config;
import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttConfiguration {
    @NotEmpty
    @JsonProperty
    private String brokerUrl;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }
}
