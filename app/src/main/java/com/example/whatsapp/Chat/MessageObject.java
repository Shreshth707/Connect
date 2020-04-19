package com.example.whatsapp.Chat;

public class MessageObject {
    String messageId,senderId,message,senderName;


    public MessageObject(String senderId,String message,String senderName){
        this.senderId = senderId;
        this.message = message;
        this.senderName = senderName;
    }
    public MessageObject(String messageId,String senderId,String message,String senderName){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.senderName = senderName;
    }



    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }
}
