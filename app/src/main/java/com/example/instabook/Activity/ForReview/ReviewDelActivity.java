package com.example.instabook.Activity.ForReview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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

import static com.example.instabook.Activity.ForReview.ModiReviewActivity.retroBaseApiService;

public class ReviewDelActivity extends Activity {

    TextView tit;
    TextView con;
    TextView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_noti_dialog);

        Intent intent = getIntent();
        String isbn = intent.getStringExtra("isbn");
        int uid = intent.getIntExtra("uid",0);

        tit = (TextView)findViewById(R.id.tv_title);
        con = (TextView)findViewById(R.id.tv_content);
        btn = (TextView)findViewById(R.id.btn_ok);

        tit.setText("삭제");
        con.setText("리뷰를 삭제하시겠습니까?");
        btn.setText("확인");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit del_retro = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = del_retro.create(RetroBaseApiService.class);

                retroBaseApiService.delRev(uid, isbn).enqueue(new Callback<ReviewData>() {
                    @Override
                    public void onResponse(Call<ReviewData> call, Response<ReviewData> response) {
                        Toast.makeText(getApplicationContext(), "리뷰 삭제 성공", Toast.LENGTH_SHORT).show();
                        finish();
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