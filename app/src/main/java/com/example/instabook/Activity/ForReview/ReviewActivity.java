package com.example.instabook.Activity.ForReview;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Fragment.SearchFragment;
import com.example.instabook.R;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity {

    public static RetroBaseApiService retroBaseApiService;
    private static final String TAG = "ReviewActivity";
    SaveSharedPreference sp;

    private RatingBar ratingBar;
    FrameLayout binfo_fr_back;
    ImageView binfo_back;
    TextView tvTitle;
    TextView tvRating;
    ImageView imBook, sbtn;
    EditText edReview;
    EditText edTag;
    Button pbtn;
    String title;
    String isbn;
    String review;
    String commentstag = null;
    ArrayList<int[]> hashtagSpans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        binfo_back = findViewById(R.id.binfo_back);
        binfo_fr_back = findViewById(R.id.binfo_fr_back);
        tvTitle = findViewById(R.id.text_title);
        tvRating = findViewById(R.id.text_rating);
        imBook = findViewById(R.id.img_book);
        ratingBar = findViewById(R.id.ratingbarIndicator);
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
        //별점 받아오기
        int rate = (int) ratingBar.getRating();

        //hashtagSpans = new ArrayList<>();
        //hashtagSpans = getSpans(commentstag, '#');
        //태그 저장
                /*
                if(edTag.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "태그를 입력하세요", Toast.LENGTH_SHORT).show();;
                } else {
                    //받아온 text를 string으로 저장
                    commentstag = edTag.getText().toString();
                }   */



        //게시하기 버튼
        pbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edReview.getText().toString().length() == 0) {  //공백일 때 처리할 내용
                    Toast.makeText(getApplicationContext(), "리뷰를 입력하세요", Toast.LENGTH_SHORT).show();
                } else { //공백이 아닐 때 처리할 내용
                    //리뷰값 저장
                    review = edReview.getText().toString();
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("Review", review);
                map.put("ISBN13", isbn);
                map.put("UserUID", useruid);
                map.put("Rate", rate);
                Log.d(TAG," 리뷰 "+review+" isbn "+isbn+" 별점 "+rate);

                //리뷰 업로드
                Retrofit rview_retro = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = rview_retro.create(RetroBaseApiService.class);

                retroBaseApiService.postReview(map).enqueue(new Callback<ReviewData>() {
                    @Override
                    public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                        Toast.makeText(getApplicationContext(), "리뷰 올리기 성공", Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(in);
                    }

                    @Override
                    public void onFailure(Call<ReviewData> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "리뷰 올리기 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //공유하기 버튼
        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edReview.getText().toString().length() == 0) {  //공백일 때 처리할 내용
                    Toast.makeText(getApplicationContext(), "리뷰를 입력하세요", Toast.LENGTH_SHORT).show();
                } else { //공백이 아닐 때 처리할 내용
                    //리뷰값 저장
                    review = edReview.getText().toString();
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("Review", review);
                map.put("ISBN13", isbn);
                map.put("UserUID", useruid);
                map.put("Rate", rate);

                //리뷰 공유
                DialogInterface.OnClickListener share = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareKakao(map, useruid);
                    }
                };
                //취소
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(ReviewActivity.this)
                        .setTitle("카카오톡으로 리뷰를 공유하시겠습니까?")
                        .setPositiveButton("공유", share)
                        .setNegativeButton("취소", cancel)
                        .show();
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


    public void shareKakao(HashMap<String, Object> map, int uuid){

        //리뷰 업로드하고 리뷰UID값 가져오기
        Retrofit retro_ruid = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_ruid.create(RetroBaseApiService.class);

        retroBaseApiService.postRUID(map).enqueue(new Callback<List<ResponseGet>>() {
            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {

                List<ResponseGet> uid_data = response.body();
                int ruid = uid_data.get(0).ReviewUID;

                //UserUID와 ReviewUID를 카카오링크 파라미터로 전송
                kakaolink(ruid, uuid);
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {

            }
        });
    }



    public void kakaolink(int ruid, int uuid) {

        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("INSTABOOK",
                        "",
                        LinkObject.newBuilder().setMobileWebUrl("kakao1b49a64d64a60eb3b9e5b61ea43a662a://kakaolink").build())
                        .setDescrption("리뷰를 확인해보세요!")
                        .build())
                .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                        .setMobileWebUrl("kakao1b49a64d64a60eb3b9e5b61ea43a662a://kakaolink")
                        .setAndroidExecutionParams("ruid="+ruid+"&uuid="+uuid)
                        .build()))
                .build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });


    }
}