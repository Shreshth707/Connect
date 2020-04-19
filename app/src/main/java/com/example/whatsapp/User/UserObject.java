package com.example.whatsapp.User;

import java.io.Serializable;

public class UserObject implements Serializable {
    private String name,phone,Uid,notificationKey,profileImageLink = "";
    private Boolean selected = false;

    public UserObject(String Uid){
        this.Uid = Uid;
    }
    public UserObject(String name,String phone,String Uid){
        this.name = name;
        this.phone = phone;
        this.Uid = Uid;
    }





    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public void setProfileImageLink(String profileImageLink) {
        this.profileImageLink = profileImageLink;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getName() { return name;    }
    public String getPhone() { return phone;    }
    public String getUid() {
        return Uid;
    }
    public String getNotificationKey() {
        return notificationKey;
    }
    public Boolean getSelected() {
        return selected;
    }

    public String getProfileImageLink() {
        return profileImageLink;
    }
}
