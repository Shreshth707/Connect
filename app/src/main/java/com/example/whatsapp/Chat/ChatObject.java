package com.example.whatsapp.Chat;

import com.example.whatsapp.User.UserObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject implements Serializable {
    private String chatId,chatName,chatIconLink = "";
    private ArrayList<UserObject> userObjectArrayList = new ArrayList<>();

    public ChatObject(String chatId,String chatName){
        this.chatId = chatId;
        this.chatName = chatName;
    }

    public void setChatIconLink(String chatIconLink) {
        this.chatIconLink = chatIconLink;
    }
    public void setName(String chatName) {
        this.chatName = chatName;
    }
    public String getChatId() {
        return chatId;
    }
    public String getName() {
        return chatName;
    }
    public ArrayList<UserObject> getUserObjectArrayList() {
        return userObjectArrayList;
    }

    public String getChatIconLink() {
        return chatIconLink;
    }

    public void addUserToArrayList(UserObject mUser){
        userObjectArrayList.add(mUser);
    }
}
