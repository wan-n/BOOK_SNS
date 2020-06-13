package com.example.instabook.Activity.MakeDeepLink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.ListView.SearchFriendItem;
import com.example.instabook.R;
import com.facebook.applinks.AppLinkData;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.AppLinks;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LinkActivity extends AppCompatActivity {

    RetroBaseApiService retroBaseApiService;
    private FrameLayout share_fr_back;
    private ImageView uf_icon;
    private TextView txt_nick, txt_date, txt_bname, txt_review, txt_tag, top_name;
    private RatingBar ratingbarSmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        share_fr_back = findViewById(R.id.share_fr_back);
        uf_icon = findViewById(R.id.uf_icon);
        txt_nick = findViewById(R.id.txt_nick);
        txt_date = findViewById(R.id.txt_date);
        txt_bname = findViewById(R.id.txt_bname);
        txt_review = findViewById(R.id.txt_review);
        txt_tag = findViewById(R.id.txt_tag);
        ratingbarSmall = findViewById(R.id.ratingbarSmall);
        top_name = findViewById(R.id.top_name);



        //뒤로가기를 누르면 홈 화면으로 이동
        share_fr_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String review_uid = null;
        String user_uid = null;
        Intent intent = getIntent();
        if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            assert data != null;
            review_uid = data.getQueryParameter("ruid");
            user_uid = data.getQueryParameter("uuid");
        }
        //리뷰 UID, 유저 UID
        int ruid = Integer.parseInt(review_uid);
        int uuid = Integer.parseInt(user_uid);


        //리뷰 내용 받아와서 화면에 표시하기
        Retrofit retro = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro.create(RetroBaseApiService.class);


        //1.ReviewUID로 리뷰내용 가져오기
        retroBaseApiService.getReview(ruid).enqueue(new Callback<List<HomeData>>() {
            @Override
            public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                List<HomeData> get_rv = response.body();
                boolean del;
                //삭제된 리뷰일 시 화면처리
                if(get_rv.get(0).isDeleted){
                    Intent Success = new Intent(getApplicationContext(), DelLinkActivity.class);
                    startActivity(Success);

                }else {
                    txt_bname.setText(get_rv.get(0).BookName);  //도서명
                    txt_review.setText(get_rv.get(0).Review);   //리뷰내용
                    ratingbarSmall.setNumStars(get_rv.get(0).Rate);  //별점

                    try {
                        Date date = new SimpleDateFormat("YYYY-MM-DD'T'HH:MM:SS.mmm'Z'").parse(get_rv.get(0).ReviewDate);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        String newdate = sdf.format(date);

                        txt_date.setText(newdate); //날짜
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //태그 추가



                }

            }

            @Override
            public void onFailure(Call<List<HomeData>> call, Throwable t) {
                //Toast.makeText(LinkActivity.this, "오류", Toast.LENGTH_SHORT).show();
            }
        });



        //2.UserUID로 이미지 가져오기
        retroBaseApiService.getImage(uuid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //서버에서 받아온 이미지 비트맵으로 변환
                InputStream is = response.body().byteStream();
                Bitmap bitmap_profile = BitmapFactory.decodeStream(is);

                uf_icon.setImageBitmap(bitmap_profile);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(LinkActivity.this, "오류", Toast.LENGTH_SHORT).show();
            }
        });

        //3.UserUID로 닉네임 가져오기
        retroBaseApiService.getUsername(uuid).enqueue(new Callback<List<ResponseGet>>() {
            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                List<ResponseGet> get_nickname = response.body();
                String name = get_nickname.get(0).getNickName();

                txt_nick.setText(name);
                top_name.setText(name+"님의 리뷰");
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                //Toast.makeText(LinkActivity.this, "오류", Toast.LENGTH_SHORT).show();
            }
        });




    }


    //뒤로가기를 누르면 홈화면으로 이동
    @Override
    public void onBackPressed(){
        Intent Success = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(Success);
    }


}

