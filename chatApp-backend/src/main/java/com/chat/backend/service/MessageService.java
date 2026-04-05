package com.chat.backend.service;

import com.chat.backend.dao.MessageDao;
import com.chat.backend.dao.UserChatDao;
import com.chat.backend.model.MessagePacket;
import com.chat.backend.utils.Logger;
import java.util.Optional;

public class MessageService {
    private final UserChatDao userChatDao;
    private final MessageDao messageDao;

    public MessageService(UserChatDao userChatDao, MessageDao messageDao){
        this.messageDao = messageDao;
        this.userChatDao = userChatDao;
    }

    public void saveIncommingMessages(MessagePacket message){
        Logger.debug("Attempting to save incoming message from Sender ID: " + message.getSenderId() + " to Chat ID: " + message.getChatId());
        
        try {
            // 1. Validate sender belongs to chat_members
            Logger.debug("Validating if user " + message.getSenderId() + " is in chat " + message.getChatId());
            Optional<Integer> isMember = userChatDao.isUserInChat(message.getChatId(), message.getSenderId());
            
            if (isMember.isPresent()) {
                Long messageId = System.currentTimeMillis() + (long)(Math.random() * 1000);

                // TODO: We can use batching to prevent frequent DB Calls
                Logger.debug("Inserting message into database with Message ID: " + messageId);
                messageDao.insertMessage(messageId, message.getChatId(), message.getSenderId(), message.getMessageContent());
                Logger.info("Message saved successfully with ID: " + messageId + " for Chat ID: " + message.getChatId());
                
                // 2. Update read tracking for the sender since they just sent a message
                Logger.debug("Updating last read message for Sender ID: " + message.getSenderId() + " in Chat ID: " + message.getChatId());
                userChatDao.updateLastReadMessage(message.getChatId(), message.getSenderId(), messageId);
                
                // 3. Update last message ID for the chat
                Logger.debug("Updating last message ID for Chat ID: " + message.getChatId());
                userChatDao.updateLastMessage(message.getChatId(), messageId);
            } else {
                Logger.error("Validation Failed: User " + message.getSenderId() + " is not a member of chat " + message.getChatId() + ". Message discarded.");
            }
        } catch (Exception e) {
            Logger.error("Exception occurred while processing and saving incoming message from Sender ID: " + message.getSenderId() + " to Chat ID: " + message.getChatId(), e);
        }
    }   
}
