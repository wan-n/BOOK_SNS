package com.example.instabook.Activity.Pre;


//JSON이 GSON 형식으로 변환
public class ResponseGet {
    public String completed;
    public int UserUID;
    public String UserID;
    public String Password;
    public String Email;
    public int PasswordQuestionUID;
    public String PasswordAnswer;
    public Boolean isDeleted;
    public String NickName;
    public int isPublic;
    public int ReviewUID;

    public ResponseGet(String completed, String UserID, String Password) {
        this.completed = completed;
        this.UserID = UserID;
        this.Password = Password;
    }

    public String getCompleted(){ return completed; }

    public int getUserUID(){return UserUID;}

    public String getEmail(){ return Email;}

    public String getPassword(){
        return Password;
    }

    public String getUserId() {
        return UserID;
    }

    public int getQeustionUID() {
        return PasswordQuestionUID;
    }

    public String getAnswer() {
        return PasswordAnswer;
    }

    public Boolean getDel(){ return isDeleted; }

    public String getNickName(){
        return NickName;
    }

    public int getIsPublic(){return isPublic;}

    public int getReviewUID(){return ReviewUID;}
}
