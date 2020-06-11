package com.example.instabook.Activity.MakeDeepLink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.example.instabook.R;

public class LinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        Intent in = getIntent();
        Uri data = in.getData();
    }
}
