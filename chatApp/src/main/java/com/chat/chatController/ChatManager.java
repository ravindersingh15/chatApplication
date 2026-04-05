package com.chat.chatController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Objects;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.paho.client.mqttv3.MqttClient;

import com.chat.api.UserChatInfo;
import com.chat.auth.User;
import com.chat.messageQueue.MessageListener;
import com.chat.model.Chat;
import com.chat.model.MessagePacket;
import com.chat.api.PresenceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatManager implements MessageListener {
    private final Scanner scanner = new Scanner(System.in);
    private final User user;
    private final MqttClient mqttClient;
    private final ChatSession chatSession;
    private final UserChatInfo chatInfo;
    private List<Chat> userChats = new ArrayList<>();
    
    private Chat currentChat = null;
    private String currentOtherUserId = null;
    private String currentPresenceTopic = null;

    public ChatManager(User user, MqttClient mqttClient) {
        this.user = user;
        this.mqttClient = mqttClient;
        this.chatInfo = new UserChatInfo(this.mqttClient);
        this.chatSession = new ChatSession(this.user, mqttClient);
    }

    private String getChatName(Chat chat) {
        String chatName = chat.getChatName();
        if (chatName != null && !chatName.isEmpty()) {
            if ("DIRECT".equalsIgnoreCase(chat.getChatType()) && chatName.contains("+")) {
                String[] names = chatName.split("\\+");
                if (names.length == 2) {
                    String myName = user.getUserName();
                    if (myName != null) {
                        return myName.equals(names[0]) ? names[1] : names[0];
                    }
                }
            }
            return chatName;
        }
        return "Chat " + chat.getChatId();
    }

    @Override
    public void onMessageReceived(String topic, String message, boolean isRetained) {
        if (topic.startsWith("presence/")) {
            handlePresenceMessage(topic, message, isRetained);
            return;
        }

        // Else continue with main "user/" flow
        MessagePacket msg = MessagePacket.deserialize(message);
        if (msg != null && msg.getChatId() != null) {
            String sender = msg.getSenderId();
            if (Objects.equals(sender, user.getUserId())) return;
            
            if (currentChat != null && currentChat.getChatId().equals(msg.getChatId())) {
                System.out.println("\n                                                                      [" + msg.getSenderUserName() + "]: " + msg.getContent());
                System.out.print("[You]: ");
            }
        }
    }

    private void handlePresenceMessage(String topic, String message, boolean isRetained) {
        // Ignore retained presence messages when opening the chat
        if (isRetained) {
            return;
        }

        try {
            if (currentOtherUserId == null || !topic.startsWith("presence/" + currentOtherUserId + "/")) {
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            PresenceStatus status = mapper.readValue(message, PresenceStatus.class);
            
            String chatName = currentChat != null ? getChatName(currentChat) : "Unknown";
            
            if (Boolean.TRUE.equals(status.getIsOnline())) {
                System.out.println("\n[Status] " + chatName + " came online");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String timeStr = sdf.format(new Date(status.getLastTime().getTime()));
                System.out.println("\n[" + timeStr + "] " + chatName + " went offline");
            }
            System.out.print("[You]: ");
        } catch (Exception e) {
            // Ignore parse errors for presence
        }
    }

    public void start() {
        while(true) {
            userChats = chatInfo.getChatList();
            if (userChats == null) {
                userChats = new ArrayList<>();
            }

            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View Chats");
            System.out.println("2. Create Direct Chat");
            System.out.println("3. Create Group Chat");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");
            
            String option = scanner.nextLine();
            
            switch (option) {
                case "1": viewChats(); break;
                case "2": createDirectChat(); break;
                case "3": createGroupChat(); break;
                case "4": return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private void viewChats() {
        if (userChats == null || userChats.isEmpty()) {
            System.out.println("No chats available.");
            return;
        }

        System.out.println("\nYour Chats:");
        for (int i = 0; i < userChats.size(); i++) {
            Chat c = userChats.get(i);
            String name = getChatName(c);
            System.out.println((i + 1) + ". " + name);
        }

        System.out.print("Select a chat to open (or 0 to cancel): ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < userChats.size()) {
                openChat(userChats.get(index));
            }
        } catch (NumberFormatException e) {
            System.out.println("Cancelled.");
        }
    }

    private void createDirectChat() {
        System.out.print("Enter user email to chat with: ");
        String email = scanner.nextLine();
        User target = chatInfo.getUserByEmail(email);
        if (target == null) {
            System.out.println("User not found.");
            return;
        }
        
        Chat newChat = chatInfo.createDirectChat(target.getUserId());
        if (newChat != null) {
            userChats.add(newChat);
            System.out.println("Direct chat created with " + target.getUserName() + "!");
        } else {
            System.out.println("Failed to create chat.");
        }
    }

    private void createGroupChat() {
        System.out.print("Enter group name: ");
        String name = scanner.nextLine();
        
        List<String> userIds = new ArrayList<>();
        userIds.add(user.getUserId());
        
        while(true) {
            System.out.print("Enter user email to add (or leave empty to finish): ");
            String email = scanner.nextLine();
            if (email.trim().isEmpty()) break;
            
            User target = chatInfo.getUserByEmail(email);
            if (target != null) {
                userIds.add(target.getUserId());
                System.out.println("Added " + target.getUserName());
            } else {
                System.out.println("User not found.");
            }
        }
        
        if (userIds.size() > 1) {
            Chat newChat = chatInfo.createGroupChat(name, userIds);
            if (newChat != null) {
                userChats.add(newChat);
                System.out.println("Group chat '" + name + "' created!");
            } else {
                System.out.println("Failed to create group.");
            }
        } else {
            System.out.println("Not enough users to create a group.");
        }
    }

    private void openChat(Chat chat) {
        this.currentChat = chat;
        String title = getChatName(chat);
        System.out.println("\n--- Opened Chat: " + title + " ---");
        
        if ("DIRECT".equalsIgnoreCase(chat.getChatType()) && chat.getMemberIds() != null) {
            for (String memberId : chat.getMemberIds()) {
                if (!memberId.equals(user.getUserId())) {
                    currentOtherUserId = memberId;
                    break;
                }
            }
            if (currentOtherUserId != null) {
                String lastSeen = chatInfo.getUserLastSeen(currentOtherUserId);
                System.out.println("[Status]: " + lastSeen);

                currentPresenceTopic = "presence/" + currentOtherUserId + "/#";
                chatSession.subscribe(currentPresenceTopic);
            }
        }

        System.out.println("----------------------------------");
        
        List<MessagePacket> history = chatInfo.getChatHistory(chat.getChatId());
        if (history != null) {
            for (MessagePacket msg : history) {
                if (Objects.equals(msg.getSenderId(), user.getUserId())) {
                    System.out.println("[You]: " + msg.getContent());
                } else {
                    System.out.println("                                                                      [" + msg.getSenderUserName() + "]: " + msg.getContent());
                }
            }
        }
        
        chatSession.subscribe("chat/" + chat.getChatId());
        chatSession.startChat(chat);
        chatSession.unsubscribe("chat/" + chat.getChatId());
        
        // Clean up presence subscription
        if (currentPresenceTopic != null) {
            chatSession.unsubscribe(currentPresenceTopic);
            currentPresenceTopic = null;
        }
        currentOtherUserId = null;
        this.currentChat = null;
    }
}
