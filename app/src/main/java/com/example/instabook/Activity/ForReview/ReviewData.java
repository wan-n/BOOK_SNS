package com.example.instabook.Activity.ForReview;

public class ReviewData {
    String review;
    float rating;
    String[] tag;

    public void ReviewData() {

    }

    public String getReview() { return review; }

    public void setReview(String review) { this.review = review; }

    public float getRating() { return rating; }

    public void setRating(float rating) { this.rating = rating; }

    public String[] getTag() {return tag;}

    public void setTag(String[] tag) {
        for(int i = 0; i<tag.length; i++){
            this.tag[i] = tag[i];
        }
    }
}
