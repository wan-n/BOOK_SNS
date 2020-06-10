package com.example.instabook.Activity.Pre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;
//스플래시 화면
public class FirstAuthActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstauth);

        if(SaveSharedPreference.getUserName(FirstAuthActivity.this).length() == 0){
            //쉐어드 프리퍼런스에 유저 정보가 없으면 로그인 액티비티 호출
            intent = new Intent(FirstAuthActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            //유저 정보가 있을 경우 홈 액티비티 호출
            intent = new Intent(FirstAuthActivity.this, MainActivity.class);
            intent.putExtra("STD_NUM", SaveSharedPreference.getUserName(this).toString());  //유저 아이디 가져오기
            startActivity(intent);
            this.finish();
        }
    }
}
