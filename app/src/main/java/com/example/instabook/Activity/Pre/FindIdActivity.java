package com.example.instabook.Activity.Pre;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.instabook.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindIdActivity extends AppCompatActivity {

    public RetroBaseApiService retroBaseApiService;

    private EditText findid_email;
    private Button findid_go;
    private TextView findid_pwd;
    private ImageView findid_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findid);

        findid_email = findViewById(R.id.findid_email);
        findid_go = findViewById(R.id.findid_go);
        findid_pwd = findViewById(R.id.findid_pwd);
        findid_back = findViewById(R.id.findid_back);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.findid_go:  //찾기 버튼
                        String email = findid_email.getText().toString();
                        email = email.trim();

                        //빈칸으로 제출했을 경우
                        if(email.getBytes().length <= 0){
                            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        //모두 적었을 경우
                        else{
                            //GET 으로 이메일 얻어오기
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(retroBaseApiService.Base_URL)
                                    .addConverterFactory(GsonConverterFactory.create()).build();
                            retroBaseApiService = retrofit.create(RetroBaseApiService.class);

                            retroBaseApiService.getEmail(email).enqueue(new Callback<List<ResponseGet>>() {
                                @Override
                                public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                                    Toast.makeText(getApplicationContext(), "받은 메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }

                                //입력된 이메일을 찾을 수 없는 경우
                                @Override
                                public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "올바른 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });






                        }
                        break;
                    case R.id.findid_pwd:   //비밀번호 찾기
                        Intent pwd = new Intent(getApplicationContext(), FindPwdActivity.class);
                        startActivity(pwd);
                        break;
                    case R.id.findid_back:   //뒤로가기(로그인 화면으로 이동)
                        onBackPressed();
                        break;
                }
            }
        };

        findid_go.setOnClickListener(listener);
        findid_pwd.setOnClickListener(listener);
        findid_back.setOnClickListener(listener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

