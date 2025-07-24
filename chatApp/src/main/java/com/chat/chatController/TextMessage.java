
package com.chat.chatController;
import com.chat.auth.User;
import com.chat.utils.TextMessageDTO;

public class TextMessage implements IMessage {
    public User sender;
    public User receiver;
    public String message;

    public TextMessage(User sender, User receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    @Override
    public void showReceiverMessage() {
        System.out.println("                                                                      " + sender.getUserName() + ": " + message);
    }

    @Override
    public void showSenderMessage() {
        // System.out.println(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getSerializedContent() {
        TextMessageDTO dto = new TextMessageDTO(sender, receiver, message);
        return TextMessageDTO.serialize(dto);
    }

    public static IMessage deserializeMessage(String mqttMessage) {
        TextMessageDTO dto = TextMessageDTO.deserialize(mqttMessage);
        if (dto == null) return null;

        User sender = new User(dto.senderName, dto.senderEmail, dto.senderId);
        User receiver = new User(dto.receiverName, dto.receiverEmail, dto.receiverId);
        return new TextMessage(sender, receiver, dto.message);
    }
}
