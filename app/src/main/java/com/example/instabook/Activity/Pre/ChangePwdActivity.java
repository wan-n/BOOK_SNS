package com.example.instabook.Activity.Pre;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instabook.R;

import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePwdActivity extends AppCompatActivity {

    private EditText ch_pwd, ch_conpwd;
    private Button ch_go;
    private ImageView ch_back;

    public RetroBaseApiService retroBaseApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);

        //이전화면에서 입력된 아이디 받아오기
        Intent intent = getIntent();
        final String user_id = intent.getExtras().getString("id");

        ch_pwd = findViewById(R.id.ch_pwd);
        ch_go = findViewById(R.id.ch_go);
        ch_conpwd = findViewById(R.id.ch_conpwd);
        ch_back = findViewById(R.id.ch_back);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아이디를 가져와 클릭된 뷰에대한 처리를 한다
                switch (v.getId()){
                    case R.id.ch_go:
                        final String userpwd = ch_pwd.getText().toString().trim();
                        final String userconpwd = ch_conpwd.getText().toString().trim();
                        final String userid = user_id;

                        HashMap<String, Object> userinfo = new HashMap<>();
                        userinfo.put("id", userid);
                        userinfo.put("pwd", userpwd);

                        //빈칸으로 제출했을 경우
                        if(userpwd.getBytes().length <= 0 || userconpwd.getBytes().length <= 0){
                            Toast.makeText(getApplicationContext(), "변경할 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        //비밀번호가 10글자보다 짧을경우
                        else if(userpwd.getBytes().length < 10) {
                            Toast.makeText(getApplicationContext(), "비밀번호는 10글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            //모든 칸에 입력했을 경우
                            if(userpwd.getBytes().length > 0 && userconpwd.getBytes().length > 0){
                                //입력값이 동일하지 않을 경우
                                if(userpwd.equals(userconpwd)){

                                    Retrofit ch_retro = new Retrofit.Builder()
                                            .baseUrl(retroBaseApiService.Base_URL)
                                            .addConverterFactory(GsonConverterFactory.create()).build();
                                    retroBaseApiService = ch_retro.create(RetroBaseApiService.class);

                                    //PUT 요청으로 비밀번호 변경
                                    retroBaseApiService.putPwd(userinfo).enqueue(new Callback<ResponseGet>(){
                                        @Override
                                        public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                                            Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                                            //로그인 화면으로 돌아감
                                            Intent finish = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(finish);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseGet> call, Throwable t) {
                                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "비밀번호 확인에 올바른 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        break;
                    case R.id.ch_back:
                        //비밀번호 찾기 화면으로 돌아감
                        onBackPressed();
                        break;
                }
            }
        };

        ch_back.setOnClickListener(listener);
        ch_go.setOnClickListener(listener);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

