package com.example.instabook.Activity.ForReview;

import android.content.Intent;
import android.net.sip.SipAudioCall;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Fragment.HomeFragment;
import com.example.instabook.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity {
    private static final String TAG = "ReviewActivity";
    private RatingBar ratingBar;
    FrameLayout binfo_fr_back;
    ImageView binfo_back;
    TextView tvTitle;
    TextView tvRating;
    ImageView imBook;
    EditText edReview;
    EditText edTag;
    Button pbtn;
    Button sbtn;
    String title;
    String isbn;
    String review;
    String commentstag = null;
    SaveSharedPreference sp;

    ArrayList<int[]> hashtagSpans;

    public static RetroBaseApiService retroBaseApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review2);

        binfo_back = findViewById(R.id.binfo_back);
        binfo_fr_back = findViewById(R.id.binfo_fr_back);
        tvTitle = findViewById(R.id.text_title);
        tvRating = findViewById(R.id.text_rating);
        imBook = findViewById(R.id.img_book);
        edReview = findViewById(R.id.edit_review);
        edTag = findViewById(R.id.edit_tag);
        pbtn = findViewById(R.id.push_btn);
        sbtn = findViewById(R.id.share_btn); //SNS 공유 버튼

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(ReviewActivity.this);

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

        ratingBar = findViewById(R.id.ratingbarSmall);


        //책 제목과 isbn 받아오기
        Intent intent = getIntent(); //보내온 intent를 얻는다
        tvTitle.setText(intent.getStringExtra("title"));
        title = tvTitle.getText().toString();
        isbn = intent.getStringExtra("isbn");

        //별점 점수 변화주기
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvRating.setText(rating + "점");
            }
        });

        //hashtagSpans = new ArrayList<>();
        //hashtagSpans = getSpans(commentstag, '#');

        pbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //리뷰값 저장
                String reView = null;
                if (edReview.getText().toString().length() == 0) {
                    //공백일 때 처리할 내용
                    Toast.makeText(getApplicationContext(), "리뷰를 입력하세요", Toast.LENGTH_SHORT).show();
                    ;
                } else {
                    //공백이 아닐 때 처리할 내용
                    reView = edReview.getText().toString();
                }

                //태그 저장
                /*
                if(edTag.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "태그를 입력하세요", Toast.LENGTH_SHORT).show();;
                } else {
                    //받아온 text를 string으로 저장
                    commentstag = edTag.getText().toString();
                }

                 */
                String review = reView.trim();
                int rate = (int) ratingBar.getRating();

                Log.d(TAG, "리뷰 :" + review);
                Log.d(TAG, "ISBN : " + isbn);
                Log.d(TAG, "UESRUID : " + useruid);
                Log.d(TAG, "별점 :" + String.valueOf(rate));

                HashMap<String, Object> map = new HashMap<>();

                map.put("Review", review);
                map.put("ISBN13", isbn);
                map.put("UserUID", useruid);
                map.put("Rate", rate);

                Retrofit dup_retro = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = dup_retro.create(RetroBaseApiService.class);

                retroBaseApiService.postReview(map).enqueue(new Callback<ReviewData>() {
                    @Override
                    public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                        Toast.makeText(getApplicationContext(), "리뷰 올리기 성공", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ReviewData> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "리뷰 올리기 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /*
    public ArrayList<int[]> getSpans(String body, char prefix){
        ArrayList<int[]> spans = new ArrayList<int[]>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        while (matcher.find()) {
            int[]   currnetSpan = new int[2];
            currnetSpan[0] = matcher.start();
            currnetSpan[1] = matcher.end();
            spans.add(currnetSpan);
        }
        return spans;
    }

     */

}