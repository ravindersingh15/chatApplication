package com.chat.backend.service;

import com.chat.backend.dao.UserDao;
import com.chat.backend.model.User;
import com.chat.backend.model.AuthUser;
import com.chat.backend.model.LoginResponse;
import com.chat.backend.utils.AuthUtil;
import com.chat.backend.utils.Logger;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public LoginResponse register(AuthUser user) {
        Long userId = System.currentTimeMillis() + (long)(Math.random() * 1000);
        Logger.debug("Generating new user ID for registration: " + userId);
        Logger.info("Registering user: " + user.getEmailId() + " with userId: " + userId);

        try {
            // Save to DB
            userDao.insertUser(userId, user.getUserName(), user.getEmailId(), user.getPassword());
            Logger.debug("User successfully inserted into DB: " + userId);
        } catch (Exception e) {
            Logger.error("Error inserting user into DB during registration: " + user.getEmailId(), e);
            throw e;
        }

        // Generate token
        String token;
        try {
            token = AuthUtil.generateToken(userId.toString());
            Logger.debug("Generated token for new user: " + userId);
        } catch (Exception e) {
            Logger.error("Error generating token for user: " + userId, e);
            throw e;
        }

        userDao.updateOnlineStatus(user.getUserId(), true, new Timestamp(System.currentTimeMillis()));

        // Return full login response
        return new LoginResponse(
                userId,
                user.getUserName(),
                user.getEmailId(),
                token);
    }

    public Optional<LoginResponse> login(String emailId, String password) {
        if (emailId == null || password == null) {
            Logger.error("Login attempt failed: Email or password is null");
            return Optional.empty();
        }

        Logger.info("Attempting to authorise: " + emailId);
        Logger.debug("Fetching user by email and password from DB: " + emailId);
        
        Optional<User> userOptional;
        try {
            userOptional = userDao.findByEmailAndPassword(emailId, password);
        } catch (Exception e) {
            Logger.error("Database error occurred while attempting login for: " + emailId, e);
            throw e;
        }

        if (userOptional.isEmpty()) {
            Logger.error("Login failed for user: " + emailId + " - Invalid credentials");
            return Optional.empty();
        }

        User user = userOptional.get();
        Logger.debug("User found in DB: " + user.getUserId());

        userDao.updateOnlineStatus(user.getUserId(), true, new Timestamp(System.currentTimeMillis()));

        String token;
        try {
            token = AuthUtil.generateToken(user.getUserId().toString());
            Logger.debug("Generated token for logged in user: " + user.getUserId());
        } catch (Exception e) {
            Logger.error("Error generating token during login for user: " + user.getUserId(), e);
            throw e;
        }

        Logger.info("Login successful for user: " + emailId);

        LoginResponse response = new LoginResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmailId(),
                token);

        return Optional.of(response);
    }
}
