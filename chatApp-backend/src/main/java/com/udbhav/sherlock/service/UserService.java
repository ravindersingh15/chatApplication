package com.udbhav.sherlock.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.udbhav.sherlock.dao.UserDao;
import com.udbhav.sherlock.model.User;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> getUserByEmail(String emailId) {
        Optional<User> user = userDao.getUserByEmail(emailId);
        if(user.isEmpty()) {
            return Optional.empty();
        }
        List<User> userList = userDao.getUsersByIds(Collections.singletonList(user.get().getUserId()));
        return userList.isEmpty() ? Optional.empty() : Optional.of(userList.get(0));
    }
}
