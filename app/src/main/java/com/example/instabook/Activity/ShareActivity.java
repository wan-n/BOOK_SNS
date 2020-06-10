package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.instabook.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
    }
}
