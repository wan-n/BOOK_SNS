package com.example.instabook.Activity.MakeDeepLink;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.MainActivity;
import com.example.instabook.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class DelLinkActivity extends AppCompatActivity {


    private FrameLayout share_fr_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_link);

        share_fr_back = findViewById(R.id.share_fr_back);

        //뒤로가기를 누르면 홈 화면으로 이동
        share_fr_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    //뒤로가기를 누르면 홈화면으로 이동
    @Override
    public void onBackPressed(){
        Intent Success = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(Success);
        finish();
    }
}
