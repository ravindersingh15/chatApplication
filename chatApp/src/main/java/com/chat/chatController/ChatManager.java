package com.chat.chatController;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttClient;

import com.chat.auth.User;
import com.chat.messageQueue.MessageListener;
import com.chat.model.MessagePacket;

public class ChatManager implements MessageListener{
    Scanner scanner = new Scanner(System.in);

    private User user;
    private UserChatList chatList;
    private ChatSession chatSession;

    public ChatManager(User user, MqttClient mqttClient) {
        this.user = user;
        chatList  = new UserChatList(user);
        chatSession = new ChatSession(this.user, mqttClient);
    }

    private boolean selectUser() {
        System.out.println("Enter the User Email Id to start Chat.");
        
        String EmailId = "";
        EmailId = scanner.nextLine();
        if(chatList.selectUser(EmailId)) {
            chatSession.setReceiverUser(chatList.getSelectedUser());
            return true;
        } else {
            System.out.println("No user found with email: " + EmailId);
            return this.selectUser();
        }
    }

    @Override
    public void onMessageReceived(String topic, String message) {
        IMessage msg = IMessage.deserializeMessage(message);
        if (msg != null) {
            msg.showReceiverMessage();
        }
    }

    public void start() {
        if(this.selectUser()) {
            System.out.println("Chat started with: " + chatList.getSelectedUser().getUserName());
            this.chatSession.subscribeToGeneralChat("user/" + user.getUserId()+ '/' + chatList.getSelectedUser().getUserId());
            this.chatSession.start();
        } else {
            System.out.println("❌ Unable to start chat session. Please try again.");
        }


    }
}
