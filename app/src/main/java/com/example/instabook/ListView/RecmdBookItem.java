package com.example.instabook.ListView;

import android.graphics.Bitmap;

public class RecmdBookItem {
    String BookName;
    String Publisher;
    String BookImageUrl;
    Bitmap Imgbm;
    String ISBN13;
    int UserBookUID = 0;

    public RecmdBookItem(String b, String isbn, Bitmap img, String p, int bid, String url) {
        this.BookName = b;
        this.ISBN13 = isbn;
        this.Imgbm = img;
        this.Publisher = p;
        this.UserBookUID = bid;
        this.BookImageUrl = url;
    }

    public void setUserBookUID(int userBookUID) {
        UserBookUID = userBookUID;
    }

    public int getUserBookUID() {
        return UserBookUID;
    }

    public String getRbname() {
        return BookName;
    }

    public String getRimguri() {
        return BookImageUrl;
    }

    public String getRisbn() {
        return ISBN13;
    }

    public String getRpub() {
        return Publisher;
    }

    public Bitmap getRImgbm() { return Imgbm;}
}

