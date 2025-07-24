package com.udbhav.sherlock.service;

import java.util.List;
import java.util.Collections;

import com.udbhav.sherlock.dao.UserChatDao;
import com.udbhav.sherlock.dao.UserDao;
import com.udbhav.sherlock.model.User;

public class UserChatService {
    
    private final UserChatDao userChatDao;
    private final UserDao userDao;

    public UserChatService(UserChatDao userChatDao, UserDao userDao) {
        this.userChatDao = userChatDao;
        this.userDao = userDao;
    }

    public List<User> getUserChatListHistory(String userId) {
        List<String> users = userChatDao.getOtherUserIds(userId);
        if(users.size() == 0) {
            return Collections.emptyList();
        }
        List<User> userList = userDao.getUsersByIds(users);
        return userList;
    }

}
