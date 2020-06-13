package com.example.instabook.Activity.ForUser;

public class UserBookData {
    public int UserBookUID;
    public int UserUID;
    public String ISBN13;

    public UserBookData(int ubid) {
        this.UserBookUID = ubid;
    }

    public UserBookData(int uid, String isbn) {
        this.UserUID = uid;
        this.ISBN13 = isbn;
    }

    public UserBookData(){

    }
    public int getUserBookUID() {
        return UserBookUID;
    }

    public void setUserBookUID(int i) {
        this.UserBookUID = i;
    }
}
