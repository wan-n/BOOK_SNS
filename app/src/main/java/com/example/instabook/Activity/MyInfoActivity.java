package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instabook.R;


public class MyInfoActivity extends AppCompatActivity {

    TextView minfo_id, minfo_email, mnickname_id;
    ImageView minfo_back;
    FrameLayout minfo_fr_back;

    SaveSharedPreference sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);



        //유저 아이디, UID, 닉네임 가져오기
        final String userid = sp.getUserName(MyInfoActivity.this);
        final String useremail = sp.getUserEmail(MyInfoActivity.this);
        final String username = sp.getUserNickname(MyInfoActivity.this);

        minfo_back = findViewById(R.id.minfo_back);
        minfo_id = findViewById(R.id.minfo_id);
        minfo_email = findViewById(R.id.minfo_email);
        minfo_fr_back = findViewById(R.id.minfo_fr_back);
        mnickname_id = findViewById(R.id.mnickname_id);


        //화면에 유저정보 출력
        minfo_id.setText(userid);
        minfo_email.setText(useremail);
        mnickname_id.setText(username);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //뒤로가기
                    case R.id.minfo_fr_back:
                        onBackPressed();
                        break;
                }
            }
        };
        minfo_fr_back.setOnClickListener(listener);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

}
