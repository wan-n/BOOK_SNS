package com.example.instabook.Activity.ForReview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.PUT;
import retrofit2.http.Query;

import static com.example.instabook.Activity.ForReview.ModiReviewActivity.retroBaseApiService;

public class ReviewDelActivity extends Activity {
    private static final String TAG = "ReviewDelActivity";
    TextView tit;
    TextView con;
    TextView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_noti_dialog);

        Intent intent = getIntent();
        int ruid = intent.getIntExtra("rrid",0);
        String bname = intent.getStringExtra("bname");
        String conf = "리뷰를 삭제하시겠습니까?\r\n책 제목 : "+bname;
        Log.d(TAG,"intent 받고 삭제 전 : "+ruid);
        tit = (TextView)findViewById(R.id.tv_title);
        con = (TextView)findViewById(R.id.tv_content);
        btn = (TextView)findViewById(R.id.btn_ok);

        tit.setText("삭제");
        con.setText(conf);
        btn.setText("확인");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //리뷰 isDeleted = 1로 업데이트
                Retrofit del_retro = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = del_retro.create(RetroBaseApiService.class);

                retroBaseApiService.putDel(ruid).enqueue(new Callback<ReviewData>() {
                    @Override
                    public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                        Toast.makeText(getApplicationContext(), "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        //메인 액티비티로 리로드
                        Intent in = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(in);
                    }

                    @Override
                    public void onFailure(Call<ReviewData> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "리뷰 삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

