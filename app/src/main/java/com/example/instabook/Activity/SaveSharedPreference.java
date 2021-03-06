package com.example.instabook.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class SaveSharedPreference {

    //key값
    static final String USER_NAME = "username";
    static final String USER_UID = "useruid";
    static final String USER_EMAIL = "useremail";
    static final String USER_NICKNAME = "usernickname";

    private static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //계정 정보 저장, 로그인 시 자동 로그인 여부에 따라 호출될 메소드,
    public static void setUserName(Context ctx, String userName, int userUID, String userEmail, String userNickname) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_NAME, userName);
        editor.putString(USER_EMAIL, userEmail);
        editor.putString(USER_NICKNAME, userNickname);
        editor.putInt(USER_UID, userUID);
        editor.commit();
    }

    public static void setUserNickName(Context ctx, String userNickname){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_NICKNAME, userNickname);
        editor.commit();
    }

    public static void setUserEmail(Context ctx, String userEmail){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_EMAIL, userEmail);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(USER_NAME, "");
    }
    public static int getUserUid(Context ctx) {
        return getSharedPreferences(ctx).getInt(USER_UID, -1);
    }
    public static String getUserEmail(Context ctx){
        return getSharedPreferences(ctx).getString(USER_EMAIL, "");
    }
    public static String getUserNickname(Context ctx){
        return getSharedPreferences(ctx).getString(USER_NICKNAME, "");
    }



    // 로그아웃 OR 회원탈퇴
    public static void clearUserName(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
