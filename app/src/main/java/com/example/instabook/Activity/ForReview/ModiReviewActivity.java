package com.example.instabook.Activity.ForReview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    ImageView imBook, sbtn;
    EditText edReview;
    EditText edTag;
    Button pbtn;
    String new_review;
    int rate;
    int new_rate;
    int uid;
    String isbn;
    String commentstag = null;
    SaveSharedPreference sp;

    ArrayList<int[]> hashtagSpans;

    public static RetroBaseApiService retroBaseApiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modi_review);

        Intent intent = getIntent();
        isbn = intent.getStringExtra("isbn");
        String review = intent.getStringExtra("review");
        String title = intent.getStringExtra("title");
        uid = intent.getIntExtra("uid", 0);
        rate = intent.getIntExtra("rate", 0);

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
                            Toast.makeText(getApplicationContext(), "리뷰 수정 성공", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(in);
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