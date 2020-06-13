package com.example.instabook.Activity.ForUser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.instabook.R;

public class NotiBookActivity extends Activity {

    TextView tit;
    TextView con;
    TextView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_noti_dialog);

        Intent intent = getIntent();
        tit = (TextView)findViewById(R.id.tv_title);
        con = (TextView)findViewById(R.id.tv_content);
        btn = (TextView)findViewById(R.id.btn_ok);

        tit.setText("");
        con.setText("");
        btn.setText("확인");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}