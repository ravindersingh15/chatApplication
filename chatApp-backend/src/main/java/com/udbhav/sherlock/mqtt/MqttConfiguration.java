package com.udbhav.sherlock.mqtt;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MqttConfiguration {
    @NotEmpty
    @JsonProperty
    private String brokerUrl;
    
    @NotEmpty
    @JsonProperty
    private String clientId;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
