package com.chat.backend.service;

import java.util.List;
import java.util.Optional;

import com.chat.backend.dao.UserChatDao;
import com.chat.backend.dao.UserDao;
import com.chat.backend.model.Chat;
import com.chat.backend.model.User;
import com.chat.backend.utils.Logger;

public class UserChatService {
    
    private final UserChatDao userChatDao;
    private final UserDao userDao;

    public UserChatService(UserChatDao userChatDao, UserDao userDao) {
        this.userChatDao = userChatDao;
        this.userDao = userDao;
    }

    public List<Chat> getUserChatListHistory(Long userId) {
        Logger.debug("Fetching chat list history for user ID: " + userId);
        try {
            List<Chat> chats = userChatDao.getChatsForUser(userId);
            for (Chat chat : chats) {
                List<String> memberIds = userChatDao.getChatMemberIds(chat.getChatId());
                chat.setMemberIds(memberIds);
            }
            Logger.info("Successfully fetched " + chats.size() + " chats for user ID: " + userId);
            return chats;
        } catch (Exception e) {
            Logger.error("Error fetching chat list history for user ID: " + userId, e);
            throw e;
        }
    }

    public Chat createDirectChat(Long user1Id, Long user2Id) {
        Logger.info("Attempting to create direct chat between user " + user1Id + " and user " + user2Id);
        try {
            // Check if chat already exists
            Logger.debug("Checking for existing direct chat between user " + user1Id + " and user " + user2Id);
            Optional<Long> existingChatId = userChatDao.findDirectChatBetweenUsers(user1Id, user2Id);
            if (existingChatId.isPresent()) {
                Logger.info("Found existing direct chat (ID: " + existingChatId.get() + ") between user " + user1Id + " and user " + user2Id);
                Chat chat = new Chat();
                chat.setChatId(existingChatId.get());
                chat.setChatType("direct");
                List<String> memberIds = userChatDao.getChatMemberIds(existingChatId.get());
                chat.setMemberIds(memberIds);
                return chat;
            }

            Long chatId = generateId();
            Logger.debug("Creating new direct chat with ID: " + chatId);
            
            // Get user names for chat name
            Optional<User> user1Opt = userDao.getUserById(user1Id);
            Optional<User> user2Opt = userDao.getUserById(user2Id);
            String chatName = "";
            if (user1Opt.isPresent() && user2Opt.isPresent()) {
                chatName = user1Opt.get().getUserName() + "+" + user2Opt.get().getUserName();
            } else {
                chatName = user1Id + "+" + user2Id; // Fallback
            }

            userChatDao.createChat(chatId, "direct", chatName, user1Id);
            
            Logger.debug("Adding user " + user1Id + " as admin to chat " + chatId);
            userChatDao.addChatMember(generateId(), chatId, user1Id, "admin");
            if (!user1Id.equals(user2Id)) {
                Logger.debug("Adding user " + user2Id + " as member to chat " + chatId);
                userChatDao.addChatMember(generateId(), chatId, user2Id, "member");
            }

            Chat chat = new Chat();
            chat.setChatId(chatId);
            chat.setChatType("direct");
            chat.setChatName(chatName);
            chat.setCreatedBy(user1Id);
            List<String> memberIds = userChatDao.getChatMemberIds(chatId);
            chat.setMemberIds(memberIds);
            Logger.info("Successfully created direct chat (ID: " + chatId + ") between user " + user1Id + " and user " + user2Id);
            return chat;
        } catch (Exception e) {
            Logger.error("Error creating direct chat between user " + user1Id + " and user " + user2Id, e);
            throw e;
        }
    }

    public Chat createGroupChat(Long creatorId, String chatName, List<Long> memberIds) {
        Logger.info("Attempting to create group chat '" + chatName + "' by user " + creatorId);
        try {
            Long chatId = generateId();
            Logger.debug("Creating new group chat with ID: " + chatId + " and name: " + chatName);
            userChatDao.createChat(chatId, "group", chatName, creatorId);

            Logger.debug("Adding creator " + creatorId + " as admin to group chat " + chatId);
            userChatDao.addChatMember(generateId(), chatId, creatorId, "admin");
            
            for (Long memberId : memberIds) {
                if (!memberId.equals(creatorId)) {
                    Logger.debug("Adding user " + memberId + " as member to group chat " + chatId);
                    userChatDao.addChatMember(generateId(), chatId, memberId, "member");
                }
            }

            Chat chat = new Chat();
            chat.setChatId(chatId);
            chat.setChatType("group");
            chat.setChatName(chatName);
            chat.setCreatedBy(creatorId);
            List<String> fetchedMemberIds = userChatDao.getChatMemberIds(chatId);
            chat.setMemberIds(fetchedMemberIds);
            Logger.info("Successfully created group chat '" + chatName + "' (ID: " + chatId + ") with " + memberIds.size() + " members");
            return chat;
        } catch (Exception e) {
            Logger.error("Error creating group chat '" + chatName + "' by user " + creatorId, e);
            throw e;
        }
    }

    private Long generateId() {
        return System.currentTimeMillis() + (long)(Math.random() * 1000);
    }
}
