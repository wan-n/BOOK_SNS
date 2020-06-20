package com.example.instabook.ListView;

import android.graphics.Bitmap;

public class HomeReviewItem {
    public Bitmap iconDrawable;
    public int uId;
    public int ruid;
    public int ubuid;
    public String review;
    public String reDate;
    public String isbn13;
    public String url;
    public Bitmap bitmap;
    public int rate;
    public String bName;
    public String nName;
    public String tags;

    public HomeReviewItem(Bitmap iconDrawable, int uid, int ruid, String url, Bitmap bitmap, String review, String redate, String isbn, int rate, String bname, String nName, String tags, int ubuid) {
        this.iconDrawable = iconDrawable;
        this.uId = uid;
        this.ruid = ruid;
        this.url = url;
        this.bitmap = bitmap;
        this.review = review;
        this.reDate = redate;
        this.isbn13 = isbn;
        this.rate = rate;
        this.bName = bname;
        this.nName = nName;
        this.tags = tags;
        this.ubuid = ubuid;
    }

    public HomeReviewItem(Bitmap img_bit, int uid, int ruid, Bitmap bitmap, String review, String redate_2, String isbn, int rate, String bname, String nname, String tags) {
        this.iconDrawable = img_bit;
        this.uId = uid;
        this.ruid = ruid;
        this.bitmap = bitmap;
        this.review = review;
        this.reDate = redate_2;
        this.isbn13 = isbn;
        this.rate = rate;
        this.bName = bname;
        this.nName = nname;
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

    public String getUrl() {
        return url;
    }

    public Bitmap getBitmap() {
        return bitmap;
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

    public int getUbuid() {
        return ubuid;
    }
}
