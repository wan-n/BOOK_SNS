package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyInfoActivity extends AppCompatActivity {

    TextView minfo_id, minfo_email;
    ImageView minfo_back;

    RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        //유저 아이디 가져오기
        final String userid = sp.getUserName(MyInfoActivity.this);

        minfo_back = findViewById(R.id.minfo_back);
        minfo_id = findViewById(R.id.minfo_id);
        minfo_email = findViewById(R.id.minfo_email);


        Retrofit retro_info = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_info.create(RetroBaseApiService.class);

        retroBaseApiService.getInfo(userid).enqueue(new Callback<List<ResponseGet>>() {
            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                    List<ResponseGet> email = response.body();
                    //화면에 유저정보 출력
                    minfo_id.setText(userid);
                    minfo_email.setText(email.get(0).getEmail());
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "페이지를 다시 로드해주세요.", Toast.LENGTH_SHORT).show();
            }
        });


        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //뒤로가기
                    case R.id.minfo_back:
                        onBackPressed();
                        break;
                }
            }
        };

        minfo_back.setOnClickListener(listener);
    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
