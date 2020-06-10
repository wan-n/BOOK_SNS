package com.example.instabook.Activity.Pre;

import com.example.instabook.Activity.ForBook.BookData;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetroBaseApiService {
    //final String Base_URL = "http://10.0.2.2:8001";
    //final String Base_URL = "http://192.168.0.35:8001";
    final String Base_URL = "http://instabookadmin.cafe24app.com";


    @Multipart
    @POST("/image/upload")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image);

    @POST("/instabook/userinfo")
    Call<ResponseGet> postReg(@Body HashMap<String, Object> parameters);//<호출할 클래스명> ResponsePet()

    @POST("/instabook/users/request")
    Call<ResponseGet> postReq(@Body HashMap<String, Object> parameters);

    @POST("/instabook/users/flist")
    Call<ResponseGet> postFrd(@Body HashMap<String, Object> parameters);

    @POST("/instabook/books/title")
    Call<BookData> postBook(@Body HashMap<String, Object> parameters);

    @POST("/instabook/books/tag")
    Call<BookData> postTag(@Query("tag") String tag);

    @GET("/instabook/books/info")
    Call<List<BookData>> getBook(@Query("keyword") String keyword);

    @GET("/instabook/books/author")
    Call<BookData> getAuthor(@Query("isbn") String isbn);

    @GET("/image/getimg")
    Call<ResponseBody> getImage(@Query("useruid") int useruid);

    @GET("/instabook/users/info")
    Call<List<ResponseGet>> getLogin(@Query("userid") String userid);

    @GET("/instabook/users/userid")
    Call<List<ResponseGet>> getDup(@Query("userid") String userid);

    @GET("/instabook/users/useremail")
    Call<List<ResponseGet>> getEmail(@Query("email") String email);

    @GET("/instabook/users/userpwd")
    Call<List<ResponseGet>> getPwd(@Query("userid") String userid);

    @GET("/instabook/users/userinfo")
    Call<List<ResponseGet>> getInfo(@Query("userid") String userid);

    @GET("/instabook/users/nickname")
    Call<List<ResponseGet>> getNickname(@Query("nickname") String nickname, @Query("userid") String userid);

    @GET("/instabook/users/req")
    Call<List<ResponseGet>> getFReq(@Query("userid") String userid);

    @GET("/instabook/users/friend")
    Call<List<ResponseGet>> getFrd(@Query("userid") String userid);

    @GET("/instabook/users/pub")
    Call<List<ResponseGet>> getPub(@Query("userid") String userid);

    @GET("/instabook/users/info/nickname")
    Call<List<ResponseGet>> getUsername(@Query("userid") String userid);

    @GET("/instabook/users/info/name")
    Call<List<ResponseGet>> getEditname(@Query("username") String username);

    @PUT("/instabook/users/userpwd")
    Call<ResponseGet> putPwd(@Body HashMap<String, Object> parameters);

    @PUT("/instabook/userinfo")
    Call<ResponseGet> putInfo(@Query("userid") String userid);

    @PUT("/instabook/userinfo/pub")
    Call<ResponseGet> putPub(@Query("userid") String userid, @Query("userpub") int userpub);

    @PUT("/instabook/userinfo/name")
    Call<ResponseGet> putName(@Query("userid") String userid, @Query("username") String username);

    @DELETE("/image/delimg")
    Call<ResponseBody> delImage(@Query("useruid") int useruid);

    @DELETE("/instabook/users/request")
    Call<ResponseGet> delReq(@Query("userid") String userid, @Query("fname") String fname);

    @DELETE("/instabook/users/friend")
    Call<ResponseGet> delFrd(@Query("userid") String userid, @Query("fname") String fname);

}

