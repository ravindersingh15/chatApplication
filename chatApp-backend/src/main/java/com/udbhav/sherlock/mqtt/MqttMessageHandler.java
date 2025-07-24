package com.udbhav.sherlock.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttMessageHandler implements MqttCallback {
    private MessageListener messageListener;
    public MqttMessageHandler(MessageListener listener){
        messageListener = listener;
    }

    @Override
    public void connectionLost(Throwable cause){
        System.out.println("🔌 Connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message){
        messageListener.onMessageReceived(topic, message.toString());
    }
    
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
