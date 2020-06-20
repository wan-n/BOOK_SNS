package com.example.instabook.ListView;

import android.graphics.Bitmap;

public class TagBookItem {
    String bname;
    String author;
    String pub;
    String pubdate;
    String url;
    String tag;
    String isbn;
    int ubuid;
    int ruid;
    Bitmap bp;

    public TagBookItem(int ruid, String tag, String isbn, String url, String bname, String pub, String pubdate, int ubuid, String author, Bitmap bp){
        this.bname = bname;
        this.author = author;
        this.pub = pub;
        this.pubdate = pubdate;
        this.url = url;
        this.tag = tag;
        this.isbn = isbn;
        this.ubuid = ubuid;
        this.ruid = ruid;
        this.bp = bp;
    }

    public String getTag() {
        return tag;
    }

    public String getBname() {
        return bname;
    }

    public String getAuthor() {
        return author;
    }

    public String getPub() {
        return pub;
    }

    public String getPubdate() {
        return pubdate;
    }

    public String getUrl() {
        return url;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getUbuid() {
        return ubuid;
    }

    public int getRuid() {
        return ruid;
    }

    public Bitmap getBp() {
        return bp;
    }
}
