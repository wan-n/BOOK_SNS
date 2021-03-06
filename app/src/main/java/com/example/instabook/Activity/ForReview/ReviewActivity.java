package com.example.instabook.Activity.ForReview;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instabook.Activity.Dialog.LogoutDialog;
import com.example.instabook.Activity.Dialog.ShareDialog;
import com.example.instabook.Activity.ForHashTag.HashTagHelper;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Activity.SettingMenuActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity implements HashTagHelper.OnHashTagClickListener, View.OnClickListener {
    private static final String TAG = "ReviewActivity";
    public static RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;
    private RatingBar ratingBar;
    private FrameLayout binfo_fr_back, sbtn;
    private ImageView binfo_back;
    private TextView tvTitle;
    private TextView tvRating;
    private ImageView imBook;
    private EditText edReview;
    private EditText edTag;
    private Button pbtn;
    private String isbn;
    private String review;
    private HashTagHelper mEditTextHashTagHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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

        //커스텀 다이얼로그 이벤트 연결
        sbtn.setOnClickListener(this);


        //유저 UID 가져오기
        final int useruid = sp.getUserUid(ReviewActivity.this);


        //뒤로가기
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
        //Bundle extras = getIntent().getExtras();

        //isbn = extras.getString("isbn");
        //byte[] byteArray = getIntent().getByteArrayExtra("img");
        //Bitmap mp = BitmapFactory.decodeByteArray(byteArray, 0 , byteArray.length);
        //imBook.setImageBitmap(mp);

        Intent intent =  getIntent();
        String t = intent.getStringExtra("title");
        String is = intent.getStringExtra("isbn");
        String url  = intent.getStringExtra("img");
        isbn = is;

        tvTitle.setText(t);

        Glide.with(this)
                .load(url)
                .error(R.drawable.default_img)
                .override(70,70)
                .into(imBook);

        //별점 점수 변화주기
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvRating.setText(rating + "점");
            }
        });

        //태그 헬퍼퍼
       mEditTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimaryDark), null);
       mEditTextHashTagHelper.handle(edTag);


        //게시하기 버튼
        pbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edReview.getText().toString().length() == 0) {  //공백일 때 처리할 내용
                    Toast.makeText(getBaseContext(), "리뷰를 입력하세요", Toast.LENGTH_SHORT).show();
                } else { //공백이 아닐 때 처리할 내용
                    //리뷰값 저장
                    review = edReview.getText().toString();

                    //별점 받아오기
                    int rate = (int) ratingBar.getRating();

                    //태그 받아오기
                    String str = edTag.getText().toString();
                    String delstr = "\\#";
                    String[] tags = str.split(delstr);


                    //리뷰 저장 hashmap
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Review", review);
                    map.put("ISBN13", isbn);
                    map.put("UserUID", useruid);
                    map.put("Rate", rate);
                    Log.d(TAG,"리뷰정보 :"+review+", "+isbn+", "+useruid+", "+rate);

                    //리뷰 업로드하고 리뷰UID값 가져오기
                    Retrofit retro_ruid = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_ruid.create(RetroBaseApiService.class);

                    retroBaseApiService.postRUID(map).enqueue(new Callback<List<ResponseGet>>() {
                        @Override
                        public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {

                            List<ResponseGet> uid_data = response.body();
                            int ruid = uid_data.get(0).getReviewUID();

                            //태그 받아오기
                            String str = edTag.getText().toString();
                            String delstr = "\\#";
                            String[] tags = str.split(delstr);

                            //태그 하나씩 저장
                            for(int i = 1; i <tags.length; i++){
                                String singletag = tags[i].trim();
                                Log.d(TAG,"singletag : "+singletag);

                                //태그 올리기
                                Retrofit tag_retro = new Retrofit.Builder()
                                        .baseUrl(retroBaseApiService.Base_URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                retroBaseApiService = tag_retro.create(RetroBaseApiService.class);

                                retroBaseApiService.postTag(ruid, singletag).enqueue(new Callback<ReviewData>() {
                                    @Override
                                    public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                                        Log.d(TAG,"태그 올리기 성공");
                                        Toast.makeText(getBaseContext(), "리뷰 올리기 성공", Toast.LENGTH_SHORT).show();

                                        Intent in = new Intent(ReviewActivity.this, MainActivity.class);
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
                        public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                            Log.d(TAG,"리뷰 올리기 실패");
                        }
                    });
                }
            }
        });

    }


    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }


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
                int ruid = uid_data.get(0).getReviewUID();


                //태그 받아오기
                String str = edTag.getText().toString();
                String delstr = "\\#";
                String[] tags = str.split(delstr);
                //태그 하나씩 저장
                for(int i = 1; i <tags.length; i++){
                    String singletag = tags[i].trim();
                    Log.d(TAG,"singletag : "+singletag);
                    //태그 올리기
                    Retrofit tag_retro = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = tag_retro.create(RetroBaseApiService.class);

                    retroBaseApiService.postTag(ruid, singletag).enqueue(new Callback<ReviewData>() {
                        @Override
                        public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                            Log.d(TAG,"태그 올리기 성공");
                            Toast.makeText(getBaseContext(), "리뷰 올리기 성공", Toast.LENGTH_SHORT).show();

                            //UserUID와 ReviewUID를 카카오링크 파라미터로 전송
                            kakaolink(ruid, uuid);
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

    @Override
    public void onHashTagClicked(String hashTag) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_btn:
                ShareDialog sdialog = new ShareDialog(this);
                sdialog.setDialogListener(new ShareDialog.ShareDialogListener() {
                    @Override
                    public void onPositiveClicked() {
                        int useruid = SaveSharedPreference.getUserUid(ReviewActivity.this);

                        if (edReview.getText().toString().length() == 0) {  //공백일 때 처리할 내용
                            Toast.makeText(getBaseContext(), "리뷰를 입력하세요", Toast.LENGTH_SHORT).show();
                        } else { //공백이 아닐 때 처리할 내용
                            //리뷰값 저장
                            review = edReview.getText().toString();
                        }
                        //별점 받아오기
                        int rate = (int) ratingBar.getRating();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("Review", review);
                        map.put("ISBN13", isbn);
                        map.put("UserUID", useruid);
                        map.put("Rate", rate);

                        shareKakao(map, useruid);
                    }

                    @Override
                    public void onNegativeClicked() {
                    }
                });
                sdialog.show();
                break;
        }
    }
}