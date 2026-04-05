package com.chat.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PresenceStatus {

    @JsonProperty("is_online")
    private Boolean isOnline;

    @JsonProperty("last_time")
    private Timestamp lastTime;

    public PresenceStatus() {
    }

    public PresenceStatus(Boolean isOnline, Timestamp lastTime) {
        this.isOnline = isOnline;
        this.lastTime = lastTime;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
    }
}