package com.chat.chatController;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;

import com.chat.api.UserChatInfo;
import com.chat.auth.User;

public class UserChatList {
    private User user;
    private User selectedUser;
    private ChatHistory history; 
    
    public UserChatList(User user) {
        this.user = user;
        history = new ChatHistory(user);
        this.getUserChatList(this.user);
    }

    public User getSelectedUser(){
        return this.selectedUser;
    }
    public boolean selectUser(String Email) {
        UserChatInfo userChatInfo = new UserChatInfo();
        User user = userChatInfo.getUserByEmail(Email);
        if (user != null) {
            this.selectedUser = user;
            history.printChatHistory(selectedUser);
            return true;
        }
        else {
            return false;
        }
    }
    

    private void printUserChatList(List<User> list) {
        System.out.println("User Chat List:"); 
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);
            System.out.println(" " + i + ". " + user.getUserName() + " (" + user.getEmailId() + ")");
        }
    }
    private List<User> getUserChatList(User user) {
        //fetchUser;
        UserChatInfo userChatInfo = new UserChatInfo();
        List<User> userList = userChatInfo.getChatList();
        this.printUserChatList(userList);
        return userList;
    }
}
