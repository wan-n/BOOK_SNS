package com.example.instabook.Activity.ForReview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModiReviewActivity extends AppCompatActivity {
    private static final String TAG = "ModiReviewActivity";
    private RatingBar ratingBar;
    FrameLayout binfo_fr_back;
    ImageView binfo_back;
    TextView tvTitle;
    TextView tvRating;
    ImageView imBook;
    EditText edReview;
    EditText edTag;
    Button pbtn;
    String new_review;
    int rate;
    int uid;
    int rid;
    String isbn;
    SaveSharedPreference sp;

    ArrayList<int[]> hashtagSpans;

    public static RetroBaseApiService retroBaseApiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modi_review);

        Intent intent = getIntent();
        isbn = intent.getStringExtra("isbn");
        String tags = intent.getStringExtra("tags");
        String review = intent.getStringExtra("review");
        String title = intent.getStringExtra("title");
        uid = intent.getIntExtra("uid", 0);
        rid = intent.getIntExtra("rid",0);
        rate = intent.getIntExtra("rate", 0);
        Log.d(TAG,"intent 받고 수정 전 : "+rid+", "+tags);
        binfo_back = findViewById(R.id.binfo_back);
        binfo_fr_back = findViewById(R.id.binfo_fr_back);
        tvTitle = findViewById(R.id.text_title);
        tvRating = findViewById(R.id.text_rating);
        imBook = findViewById(R.id.img_book);
        edReview = findViewById(R.id.edit_review);
        edTag = findViewById(R.id.edit_tag);
        pbtn = findViewById(R.id.push_btn);
        ratingBar = findViewById(R.id.ratingbarIndicator);

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(ModiReviewActivity.this);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.binfo_fr_back:
                        onBackPressed();
                        break;
                }
            }
        };
        binfo_fr_back.setOnClickListener(listener);

        tvTitle.setText(title);
        edReview.setText(review);
        ratingBar.setNumStars(rate);
        edTag.setText(tags);

        //별점 점수 변화주기
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvRating.setText(rating + "점");
            }
        });


        //게시하기 버튼
        pbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //리뷰값 저장
                String reView;
                if (edReview.getText().toString().length() == 0) {
                    //공백일 때 처리할 내용
                    Toast.makeText(getApplicationContext(), "리뷰를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    //공백이 아닐 때 처리할 내용
                    reView = edReview.getText().toString();
                    new_review = reView.trim();
                    int new_rate = (int) ratingBar.getRating();

                    //태그 받아오기
                    String str = edTag.getText().toString();
                    String delstr = "\\#";
                    String[] tags = str.split(delstr);

                    HashMap<String, Object> map = new HashMap<>();

                    map.put("Review", new_review);
                    map.put("ISBN13", isbn);
                    map.put("UserUID", uid);
                    map.put("Rate", new_rate);
                    Log.d(TAG, " ~~ "+new_review+" ~~ "+new_rate+" ~~ "+isbn+" ~~ "+uid);

                    //리뷰 업데이트
                    Retrofit re_retro = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = re_retro.create(RetroBaseApiService.class);

                    retroBaseApiService.putMoRe(map).enqueue(new Callback<ReviewData>() {
                        @Override
                        public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                            //태그 받아오기
                            String str = edTag.getText().toString();
                            String delstr = "\\#";
                            String[] tags = str.split(delstr);

                            //태그 하나씩 저장
                            for(int i = 1; i <tags.length; i++){
                                String singletag = tags[i];
                                Log.d(TAG,"singletag : "+singletag);

                                //태그 올리기
                                Retrofit tag_retro = new Retrofit.Builder()
                                        .baseUrl(retroBaseApiService.Base_URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                retroBaseApiService = tag_retro.create(RetroBaseApiService.class);

                                retroBaseApiService.postTag(rid, singletag).enqueue(new Callback<ReviewData>() {
                                    @Override
                                    public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                                        Log.d(TAG,"태그 올리기 성공");
                                        Toast.makeText(getBaseContext(), "리뷰 올리기 성공", Toast.LENGTH_SHORT).show();

                                        Intent in = new Intent(getBaseContext(), MainActivity.class);
                                        startActivity(in);
                                    }
                                    @Override
                                    public void onFailure(Call<ReviewData> call, Throwable t) {
                                        Log.d(TAG,"태그 올리기 실패");
                                    }
                                });
                            }
                        }
                        @Override
                        public void onFailure(Call<ReviewData> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "리뷰 수정 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
