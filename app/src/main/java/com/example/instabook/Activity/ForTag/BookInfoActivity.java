package com.example.instabook.Activity.ForTag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.ForHashTag.HashTagHelper;
import com.example.instabook.Activity.ForHashTag.Hashtag;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookInfoActivity extends AppCompatActivity implements HashTagHelper.OnHashTagClickListener{
    private static final String TAG = "ReviewActivity";
    public static RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;
    int ubuid;
    String title;
    String isbn;
    private int useruid;
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
        byte[] b = intent.getByteArrayExtra("bm");
        title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        isbn = intent.getStringExtra("isbn");
        String pub = intent.getStringExtra("pub");
        String pubdate = intent.getStringExtra("pubdate");
        String tag = intent.getStringExtra("tag");
        ubuid = intent.getIntExtra("ubuid",0);

        Bitmap bp = BitmapFactory.decodeByteArray(b,0,b.length);

        bi_fr_back = findViewById(R.id.bi_fr_back);
        bi_back = findViewById(R.id.bi_back);
        imBook = findViewById(R.id.img_book);
        tvTitle = findViewById(R.id.txt_title);
        tvAuthor = findViewById(R.id.txt_author);
        tvPub = findViewById(R.id.txt_pub);
        tvPubdate = findViewById(R.id.txt_pubdate);
        tvTag = findViewById(R.id.txt_tag);
        btnChoice = findViewById(R.id.btn_choice);

        //유저 UID 가져오기
        useruid = sp.getUserUid(BookInfoActivity.this);

        imBook.setImageBitmap(bp);
        tvTitle.setText(title);
        tvAuthor.setText(author);
        tvPub.setText(pub);
        tvPubdate.setText(pubdate);

        //태그
        ArrayList<int[]> hashtagSpan = getSpans(tag,'#');
        SpannableString commentsContent = new SpannableString(tag);
        setSpanComment(commentsContent,hashtagSpan) ;
        tvTag.setMovementMethod(LinkMovementMethod.getInstance());
        tvTag.setText(commentsContent);


        btnChoice.setOnClickListener(ChoiceOnClickListener);


    }

        final Button.OnClickListener ChoiceOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("uid",useruid);
                map.put("isbn",isbn);


                if(ubuid == 0){
                    Retrofit retro_jjim = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_jjim.create(RetroBaseApiService.class);

                    retroBaseApiService.postUBook(map).enqueue(new Callback<UserBookData>() {
                        @Override
                        public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                            Toast.makeText(getApplicationContext(), title+" 찜 도서 추가 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override
                        public void onFailure(Call<UserBookData> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "찜 도서 추가 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                }
            }
        };

    @Override
    public void onHashTagClicked(String hashTag) {

    }

    public ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }

        return  spans;
    }


    private void setSpanComment(SpannableString commentsContent, ArrayList<int[]> hashtagSpans) {
        for(int i = 0; i < hashtagSpans.size(); i++) {
            int[] span = hashtagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];

            commentsContent.setSpan(new Hashtag(this), hashTagStart, hashTagEnd, 0);

        }

    }
}