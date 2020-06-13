package com.example.instabook.Activity.ForHome;

public class AllUserData {
    public int UserUID;

    public AllUserData(int uid){
        this.UserUID = uid;
    }
    public AllUserData(){

    }

    public int getUserUID() {
        return UserUID;
    }

    public void setUserUID(int userUID) {
        UserUID = userUID;
    }
}