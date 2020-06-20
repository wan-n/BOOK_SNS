package com.example.instabook.Activity.ForBook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.instabook.R;

public class BookInfoActivity extends AppCompatActivity {

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