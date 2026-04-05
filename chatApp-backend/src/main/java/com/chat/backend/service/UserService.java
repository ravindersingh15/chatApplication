package com.chat.backend.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.chat.backend.dao.UserDao;
import com.chat.backend.model.User;
import com.chat.backend.utils.Logger;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> getUserByEmail(String emailId) {
        Logger.debug("Attempting to fetch user by email: " + emailId);
        try {
            Optional<User> user = userDao.getUserByEmail(emailId);
            if(user.isEmpty()) {
                Logger.info("User not found with email: " + emailId);
                return Optional.empty();
            }
            
            Logger.debug("User found, fetching full details for user ID: " + user.get().getUserId());
            List<User> userList = userDao.getUsersByIds(Collections.singletonList(user.get().getUserId()));
            
            if (userList.isEmpty()) {
                Logger.error("Failed to fetch full details for user ID: " + user.get().getUserId());
                return Optional.empty();
            }
            
            Logger.info("Successfully fetched full details for user with email: " + emailId);
            return Optional.of(userList.get(0));
        } catch (Exception e) {
            Logger.error("Error occurred while fetching user by email: " + emailId, e);
            throw e;
        }
    }
}