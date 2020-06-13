package com.example.instabook.Activity.ForHome;

public class UserData {
    public int UserUID;
    public int FriendUID;

    public UserData(int uid, int fuid){
        this.UserUID = uid;
        this.FriendUID = fuid;
    }

    public UserData() {

    }

    public void setUserUID(int uid) {
        this.UserUID = uid;
    }
    public void setFriendUID(int friendUID) {
        FriendUID = friendUID;
    }
    public int getUserUID() { return UserUID; }
    public int getFriendUID() {
        return FriendUID;
    }
}
