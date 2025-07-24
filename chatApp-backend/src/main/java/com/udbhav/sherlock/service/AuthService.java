package com.udbhav.sherlock.service;

import com.udbhav.sherlock.dao.AuthUserDao;
import com.udbhav.sherlock.dao.UserDao;
import com.udbhav.sherlock.model.AuthUser;
import com.udbhav.sherlock.model.LoginResponse;
import com.udbhav.sherlock.model.User;
import com.udbhav.sherlock.utils.AuthUtil;
import com.udbhav.sherlock.utils.Logger;

import java.util.Optional;
import java.util.UUID;

public class AuthService {

    private final AuthUserDao userDao;

    public AuthService(AuthUserDao userDao) {
        this.userDao = userDao;
    }

    public LoginResponse register(AuthUser user) {
        String userId = UUID.randomUUID().toString();
        Logger.info("Registering user: " + user.getEmailId() + " with userId: " + userId);

        // Save to DB
        userDao.insertUser(userId, user.getUserName(), user.getEmailId(), user.getPassword());

        // Generate token
        String token = AuthUtil.generateToken(userId);

        // Return full login response
        return new LoginResponse(
                userId,
                user.getUserName(),
                user.getEmailId(),
                token);
    }

    public Optional<LoginResponse> login(String emailId, String password) {
        if (emailId == null || password == null) {
            return Optional.empty();
        }

        Logger.info("Attempting to authorise: " + emailId + " with password: " + password);
        Optional<AuthUser> userOptional = userDao.findByEmailAndPassword(emailId, password);

        if (userOptional.isEmpty()) {
            Logger.error("Login failed for user: " + emailId);
            return Optional.empty();
        }

        AuthUser user = userOptional.get();
        String token = AuthUtil.generateToken(user.getUserId());

        Logger.info("Login successful for user: " + emailId + " with token: " + token);

        LoginResponse response = new LoginResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmailId(),
                token);

        return Optional.of(response);
    }
}
