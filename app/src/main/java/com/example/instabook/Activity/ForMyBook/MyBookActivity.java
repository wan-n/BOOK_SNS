package com.example.instabook.Activity.ForMyBook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;

public class MyBookActivity extends AppCompatActivity {
    public static RetroBaseApiService retroBaseApiService;
    private static final String TAG = "ReviewActivity";
    SaveSharedPreference sp;
    FrameLayout binfo_fr_back;
    ImageView binfo_back;
    ImageView imbimg;
    TextView tvTitle;
    TextView tvAuthor;
    TextView tvPub;
    ImageButton pbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybook);

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(MyBookActivity.this);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.binfo_fr_back:
                        onBackPressed();
                        break;
                }
            }
        };
        binfo_fr_back.setOnClickListener(listener);

        Intent intent = getIntent();

        binfo_back = findViewById(R.id.binfo_back);
        binfo_fr_back = findViewById(R.id.binfo_fr_back);
        imbimg = findViewById(R.id.mb_icon);
        tvTitle = findViewById(R.id.mb_title);
        tvAuthor = findViewById(R.id.mb_author);
        tvPub = findViewById(R.id.mb_pub);
        pbtn = findViewById(R.id.mb_clear);

    }


}
