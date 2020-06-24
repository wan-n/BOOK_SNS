package com.example.instabook.ListView;

import android.graphics.Bitmap;

public class UserBookItem {
    String ISBN13;
    int UserBookUID;
    String BookImageUrl;
    String BookName;
    String Publisher;
    String Author;
    Bitmap bitmap;

    public UserBookItem(String bn, String bpub, Bitmap bm, String ba, String bisbn, int buid, String url){
        this.ISBN13 = bisbn;
        this.UserBookUID = buid;
        this.bitmap = bm;
        this.BookName = bn;
        this.Publisher = bpub;
        this.Author = ba;
        this.BookImageUrl = url;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public String getBookName() {
        return BookName;
    }

    public String getAuthor() {
        return Author;
    }

    public String getPublisher() {
        return Publisher;
    }

    public int getUserBookUID() {
        return UserBookUID;
    }

    public String getBookImageUrl() {
        return BookImageUrl;
    }
}
