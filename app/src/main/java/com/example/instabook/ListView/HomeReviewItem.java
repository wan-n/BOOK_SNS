package com.example.instabook.ListView;

import android.graphics.Bitmap;

public class HomeReviewItem {
    public Bitmap iconDrawable;
    public int uId;
    public String review;
    public String reDate;
    public String isbn13;
    public int rate;
    public String bName;
    public String nName;
    public int UserBookUID;

    public HomeReviewItem(Bitmap iconDrawable, int uid, String review, String redate, String isbn, int rate, String bname, String nName) {
        this.iconDrawable = iconDrawable;
        this.uId = uid;
        this.review = review;
        this.reDate = redate;
        this.isbn13 = isbn;
        this.rate = rate;
        this.bName = bname;
        this.nName = nName;
    }

    public void setUserBookUID(int userBookUID) {
        UserBookUID = userBookUID;
    }

    public Bitmap getIconDrawable() {
        return iconDrawable;
    }

    public int getuId() {
        return uId;
    }

    public String getReview() {
        return review;
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
