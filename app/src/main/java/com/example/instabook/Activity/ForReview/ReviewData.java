package com.example.instabook.Activity.ForReview;

public class ReviewData {
    public String Review;
    public int Rate;
    public int UserUID;
    public String ISBN13;


    public ReviewData() {

    }

    public String getReview() { return Review; }

    public void setReview(String review) { Review = review; }

    public int getRating() { return Rate; }

    public void setRating(int rate) { Rate = rate; }

    public int getUserUID() { return UserUID; }

    public void setUserUID(int userUID) { UserUID = userUID; }

    public String getISBN13() { return ISBN13; }

    public void setISBN13(String isbn13) { ISBN13 = isbn13; }
}
