package com.chat.chatController;

import com.chat.messageQueue.MessageListener;

public class ReceiveMessage implements MessageListener {

    @Override
    public void onMessageReceived(String topic, String message) {
        IMessage msg = IMessage.deserializeMessage(message);
        if (msg != null) {
            msg.showReceiverMessage();
        }
    }

    
}
