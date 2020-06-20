package com.example.instabook.Activity.ForBook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.instabook.Activity.ForHashTag.HashTagHelper;
import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;

public class BookInfoActivity extends AppCompatActivity implements HashTagHelper.OnHashTagClickListener{
    private static final String TAG = "ReviewActivity";
    public static RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;
    int ubuid;
    String title;
    private FrameLayout bi_fr_back;
    private ImageView bi_back;
    private ImageView imBook;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPub;
    private TextView tvPubdate;
    private TextView tvDescription;
    private TextView tvTag;
    private Button btnChoice;
    private HashTagHelper mTextHashTagHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        Intent intent = getIntent();
        Bitmap bp = (Bitmap)intent.getParcelableExtra("bm");
        title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String pub = intent.getStringExtra("pub");
        String pubdate = intent.getStringExtra("pubdate");
        String tag = intent.getStringExtra("tag");
        ubuid = intent.getIntExtra("ubuid",0);

        bi_fr_back = findViewById(R.id.bi_fr_back);
        bi_back = findViewById(R.id.bi_back);
        imBook = findViewById(R.id.img_book);
        tvTitle = findViewById(R.id.txt_title);
        tvAuthor = findViewById(R.id.txt_author);
        tvPub = findViewById(R.id.txt_pub);
        tvPubdate = findViewById(R.id.txt_pubdate);
        tvDescription = findViewById(R.id.txt_description);
        tvTag = findViewById(R.id.txt_tag);
        btnChoice = findViewById(R.id.btn_choice);

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(BookInfoActivity.this);

        imBook.setImageBitmap(bp);
        tvTitle.setText(title);
        tvAuthor.setText(author);
        tvPub.setText(pub);
        tvPubdate.setText(pubdate);


        //태그 헬퍼퍼
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimaryDark), null);
        mTextHashTagHelper.handle(tvTag);

        btnChoice.setOnClickListener(ChoiceOnClickListener);


    }

        final Button.OnClickListener ChoiceOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    @Override
    public void onHashTagClicked(String hashTag) {

    }
}