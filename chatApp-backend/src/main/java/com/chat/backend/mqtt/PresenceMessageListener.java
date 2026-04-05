package com.chat.backend.mqtt;

import com.chat.backend.dao.UserDao;
import com.chat.backend.model.PresenceStatus;
import com.chat.backend.utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;


public class PresenceMessageListener implements MessageListener {

    private final UserDao userDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PresenceMessageListener(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void onMessageReceived(String topic, String message, boolean isRetained) {
        if (isRetained) {
            return;
        }

        try {
            Long userId = null;

            if (topic != null && topic.startsWith("presence/")) {
                String[] parts = topic.split("/");
                if (parts.length > 1) {
                    try {
                        userId = Long.parseLong(parts[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            PresenceStatus presence = objectMapper.readValue(message, PresenceStatus.class);

            if (userId != null) {

                // ✅ Use Boolean instead of String comparison
                boolean isOnline = Boolean.TRUE.equals(presence.getIsOnline());

                // ✅ Directly use Timestamp from model
                Timestamp lastSeen = presence.getLastTime() != null
                        ? presence.getLastTime()
                        : new Timestamp(System.currentTimeMillis());

                userDao.updateOnlineStatus(userId, isOnline, lastSeen);

                Logger.info("Updated user: " + userId +
                        " | isOnline: " + isOnline +
                        " | lastSeen: " + lastSeen);

            } else {
                Logger.error("Could not extract userId. Topic: " + topic + ", Message: " + message);
            }

        } catch (Exception e) {
            Logger.error("Error processing presence message: " + message, e);
        }
    }
}
