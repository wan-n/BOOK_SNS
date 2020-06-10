package com.example.instabook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.R;

public class RsubActivity extends AppCompatActivity {

    TextView receiveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsub);

        Intent intent = getIntent();
        String receiveStr = intent.getExtras().getString("sendData");
        receiveView = (TextView)findViewById(R.id.receiveText);
        receiveView.setText(receiveStr);
    }
}