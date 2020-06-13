package com.example.instabook.Activity.ForUser;

public class UserBookData {
    public Integer UserBookUID;
    public int UserUID;
    public String ISBN13;

    public UserBookData( Integer ubid) {
        this.UserBookUID = ubid;
    }

    public UserBookData(int uid, String isbn) {
        this.UserUID = uid;
        this.ISBN13 = isbn;
    }

    public Integer getUserBookUID() {
        return UserBookUID;
    }
}
