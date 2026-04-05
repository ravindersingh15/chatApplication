package com.chat.backend.model;

import java.sql.Timestamp;

public class UserStatusDTO {
    private Timestamp lastSeen;
    private boolean isOnline;

    public UserStatusDTO() {}

    public UserStatusDTO(Timestamp lastSeen, boolean isOnline) {
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Timestamp lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
