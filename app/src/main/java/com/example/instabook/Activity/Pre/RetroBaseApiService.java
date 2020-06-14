package com.example.instabook.Activity.Pre;

import com.example.instabook.Activity.ForBook.BookData;
import com.example.instabook.Activity.ForBook.NaverBookData;
import com.example.instabook.Activity.ForBook.NaverData;
import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.ForHome.UserData;
import com.example.instabook.Activity.ForReview.ReviewData;
import com.example.instabook.Activity.ForUser.UserBookData;

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



    @POST("/instabook/reviews/uid")
    Call<List<ResponseGet>> postRUID(@Body HashMap<String, Object> parameters);

    @Multipart
    @POST("/image/upload")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image);

    @POST("/instabook/userinfo")
    Call<ResponseGet> postReg(@Body HashMap<String, Object> parameters);//<호출할 클래스명> ResponsePet()

    @POST("/instabook/users/request")
    Call<ResponseGet> postReq(@Body HashMap<String, Object> parameters);

    @POST("/instabook/users/flist")
    Call<ResponseGet> postFrd(@Body HashMap<String, Object> parameters);

    @POST("/instabook/reviews/review")
    Call<ReviewData> postReview(@Body HashMap<String, Object> parameters);

    @POST("/instabook/users/userbook")
    Call<UserBookData> postUBook(@Body HashMap<String, Object> parameters);

    @POST("/instabook/books")
    Call<NaverData> postNbook(@Body HashMap<String, Object> parameters);

    /**TODO 태그데이터 서버 올리기
    @POST("/instabook/reviews/tag")
    Call<ReviewData> postTag(@Body HashMap<String, Object> parameters);
    */

    @GET("/instabook/reviews/uid")
    Call<List<UserData>> getUid(@Query("UserUID") int useruid);

    @GET("/instabook/review")
    Call<List<HomeData>> getReview(@Query("reviewuid") int reviewuid);

    @GET("/instabook/reviews/ubid")
    Call<UserBookUIDData> getUBid(@Query("useruid") int useruid, @Query("isbn") String isbn);

    @GET("/instabook/reviews/home")
    Call<List<HomeData>> getHreq(@Query("useruid") int useruid);

    @GET("/instabook/books/info")
    Call<List<BookData>> getBook(@Query("keyword") String keyword);

    @GET("/instabook/books/author")
    Call<List<BookData>> getAuthor(@Query("isbn") String isbn);

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

    @GET("/instabook/users/nickname")
    Call<List<ResponseGet>> getNickname(@Query("nickname") String nickname, @Query("userid") String userid);

    @GET("/instabook/users/req")
    Call<List<ResponseGet>> getFReq(@Query("userid") String userid);

    @GET("/instabook/users/friend")
    Call<List<ResponseGet>> getFrd(@Query("userid") String userid);

    @GET("/instabook/users/pub")
    Call<List<ResponseGet>> getPub(@Query("userid") String userid);

    @GET("/instabook/users/info/nickname")
    Call<List<ResponseGet>> getUsername(@Query("useruid") int useruid);

    @GET("/instabook/users/info/name")
    Call<List<ResponseGet>> getEditname(@Query("username") String username);

    @GET("/instabook/reviews/count")
    Call<List<ResponseGet>> getReviewcnt(@Query("useruid") int useruid);

    @PUT("/instabook/users/userpwd")
    Call<ResponseGet> putPwd(@Body HashMap<String, Object> parameters);

    @PUT("/instabook/userinfo")
    Call<ResponseGet> putInfo(@Query("userid") String userid);

    @PUT("/instabook/userinfo/pub")
    Call<ResponseGet> putPub(@Query("userid") String userid, @Query("userpub") int userpub);

    @PUT("/instabook/userinfo/name")
    Call<ResponseGet> putName(@Query("userid") String userid, @Query("username") String username);

    @PUT("/instabook/reviews/modify")
    Call<ReviewData> putMoRe(@Body HashMap<String, Object> parameters);

    @DELETE("/image/delimg")
    Call<ResponseBody> delImage(@Query("useruid") int useruid);

    @DELETE("/instabook/users/request")
    Call<ResponseGet> delReq(@Query("userid") String userid, @Query("fname") String fname);

    @DELETE("/instabook/users/friend")
    Call<ResponseGet> delFrd(@Query("userid") String userid, @Query("fname") String fname);

    @DELETE("/instabook/users/delubook")
    Call<UserBookData> delUBook(@Query("ubuid") int ubuid);

    @DELETE("/instabook/reviews/delrev")
    Call<ReviewData> delRev(@Query("uid") int uid, @Query("isbn") String isbn);

}

