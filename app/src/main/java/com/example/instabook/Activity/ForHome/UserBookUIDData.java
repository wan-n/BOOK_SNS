package com.example.instabook.Activity.ForHome;

import com.example.instabook.Activity.ForUser.UserBookData;

public class UserBookUIDData {
    public int UserBookUID;

    public UserBookUIDData(int ubuid) {
        this.UserBookUID = ubuid;
    }

    public UserBookUIDData(){

    }

    public int getUserBookUID() {
        return UserBookUID;
    }

    public void setUserBookUID(int userBookUID) {
        UserBookUID = userBookUID;
    }
}
