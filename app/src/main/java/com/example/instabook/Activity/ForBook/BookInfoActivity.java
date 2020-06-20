package com.example.instabook.Activity.ForBook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;

public class BookInfoActivity extends AppCompatActivity {
    private static final String TAG = "ReviewActivity";
    public static RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;
    private FrameLayout bi_fr_back;
    private ImageView bi_back;
    private ImageView imBook;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPub;
    private TextView tvPubdate;
    private TextView tvDescription;
    private TextView tvTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        Intent intent = getIntent();
        Bitmap bp = (Bitmap)intent.getParcelableExtra("bm");
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String pub = intent.getStringExtra("pub");
        String pubdate = intent.getStringExtra("pubdate");
        String tag = intent.getStringExtra("tag");
        int ubuid = intent.getIntExtra("ubuid",0);



    }
}