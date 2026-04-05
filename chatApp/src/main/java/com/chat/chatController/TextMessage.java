package com.chat.chatController;

public class TextMessage implements IMessage {
    @Override
    public void showReceiverMessage() {}

    @Override
    public void showSenderMessage() {}

    @Override
    public String getMessage() { return null; }

    @Override
    public String getSerializedContent() { return null; }

    public static IMessage deserializeMessage(String mqttMessage) {
        return null;
    }
}
