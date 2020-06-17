package com.example.instabook.ListView;

public class RecmdBookItem {
    String BookName;
    String Publisher;
    String BookImageUrl;
    String ISBN13;
    int UserBookUID = 0;

    public RecmdBookItem(String b, String isbn, String url, String p, int bid) {
        this.BookName = b;
        this.ISBN13 = isbn;
        this.BookImageUrl = url;
        this.Publisher = p;
        this.UserBookUID = bid;
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

}

