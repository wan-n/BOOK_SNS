package com.example.instabook.ListView;

import android.graphics.Bitmap;

public class HomeReviewItem {
    public Bitmap iconDrawable;
    public int uId;
    public int ruid;
    public String review;
    public String reDate;
    public String isbn13;
    public int rate;
    public String bName;
    public String nName;
    public int UserBookUID;
    public String tags;

    public HomeReviewItem(Bitmap iconDrawable, int uid, int ruid,String review, String redate,
                          String isbn, int rate, String bname, String nName, String tags) {
        this.iconDrawable = iconDrawable;
        this.uId = uid;
        this.ruid = ruid;
        this.review = review;
        this.reDate = redate;
        this.isbn13 = isbn;
        this.rate = rate;
        this.bName = bname;
        this.nName = nName;
        this.tags = tags;
    }


    public Bitmap getIconDrawable() {
        return iconDrawable;
    }

    public int getuId() {
        return uId;
    }

    public int getRuid() {
        return ruid;
    }

    public String getReview() {
        return review;
    }

    public String getTags() {
        return tags;
    }

    public String getReDate() {
        return reDate;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public int getRate() {
        return rate;
    }

    public String getbName() {
        return bName;
    }

    public String getnName() {
        return nName;
    }

    public int getUserBookUID() { return UserBookUID; }
}
