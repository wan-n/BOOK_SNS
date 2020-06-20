package com.example.instabook.Activity.ForHome;

public class HomeData {
    public int UserUID;
    public int ReviewUID;
    public String Review;
    public String ReviewDate;
    public String ISBN13;
    public int Rate;
    public String BookImageUrl;
    public String BookName;
    public String NickName;
    public boolean isDeleted;
    public String Tag;

    public HomeData(int uid, String review, String reviewDate, String isbn, int rate,
                    String bookName, String nickName){
        this.UserUID = uid;
        this.Review = review;
        this.ReviewDate = reviewDate;
        this.ISBN13 = isbn;
        this.Rate = rate;
        this.BookName = bookName;
        this.NickName = nickName;
    }

    public int getUserUID() {
        return UserUID;
    }
    public String getReview() {
        return Review;
    }
    public String getReviewDate() {
        return ReviewDate;
    }
    public String getISBN13() {
        return ISBN13;
    }
    public int getRate() {
        return Rate;
    }
    public String getBookName() {
        return BookName;
    }
    public String getNickName() {
        return NickName;
    }

    public int getReviewUID() {
        return ReviewUID;
    }

    public String getBookImageUrl() {
        return BookImageUrl;
    }

    public String getTag() {
        return Tag;
    }

    public boolean getIsDeleted(){return isDeleted;}
}
