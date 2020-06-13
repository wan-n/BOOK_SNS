package com.example.instabook.Activity.ForUser;

public class UserBookData {
    public Boolean isFinished;
    public int UserBookUID;
    public int UserUID;
    public String ISBN13;

    public UserBookData(Boolean isf, int ubid) {
        this.isFinished = isf;
        this.UserBookUID = ubid;
    }

    public UserBookData(Boolean isf, int uid, String isbn) {
        this.isFinished = isf;
        this.UserUID = uid;
        this.ISBN13 = isbn;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public int getUserBookUID() {
        return UserBookUID;
    }
}
