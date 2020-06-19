package com.example.instabook.Activity.ForHome;

public class UserData {
    public int UserUID;
    public int FriendUID;

    public UserData(int uid){
        this.UserUID = uid;
    }

    public UserData() {

    }

    public int getUserUID() { return UserUID; }
    public int getFriendUID() {
        return FriendUID;
    }
}
