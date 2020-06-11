package com.example.instabook.Activity.ForReview;

import java.util.HashMap;

public class ReviewData {
    public String Review;
    public int rate;
    public int UserUID;
    public String ISBN13;
    public Boolean isDeleted;

    public void ReviewData() {

    }

    public ReviewData() {

    }

    public String getReview() { return Review; }

    public void setReview(String review) { Review = review; }

    public float getRating() { return rate; }

    public void setRating(int rating) { rate = rating; }

    public int getUserUID() { return UserUID; }

    public void setUserUID(int userUID) { UserUID = userUID; }

    public String getISBN13() { return ISBN13; }

    public void setISBN13(String isbn13) { ISBN13 = isbn13; }
}
