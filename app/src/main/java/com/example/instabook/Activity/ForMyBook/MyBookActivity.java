package com.example.instabook.Activity.ForMyBook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.instabook.Activity.ForBook.BookData;
import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.BookListAdapter;
import com.example.instabook.Adapter.MyBookAdapter;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.ListView.UBookListItem;
import com.example.instabook.ListView.UserBookItem;
import com.example.instabook.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class MyBookActivity extends AppCompatActivity {
    public static RetroBaseApiService retroBaseApiService;
    private static final String TAG = "ReviewActivity";
    SaveSharedPreference sp;
    FrameLayout binfo_fr_back;
    ImageView binfo_back;
    List<UBookListItem> blist = new ArrayList<UBookListItem>();
    ArrayList<UserBookItem> items;
    List<BookData> authorDataList;
    List<BookData> authorlist;
    List<String> authorsam;
    String author = "";
    UserBookItem ub;
    Bitmap bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybook);

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(MyBookActivity.this);

        binfo_back = findViewById(R.id.mb_back);
        binfo_fr_back = findViewById(R.id.mb_fr_back);

        //뒤로가기 버튼
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mb_fr_back:
                        onBackPressed();
                        break;
                }
            }
        };
        binfo_fr_back.setOnClickListener(listener);

        //소유 도서에 저장된 도서 정보 가져오기
        Retrofit retro_ubook = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_ubook.create(RetroBaseApiService.class);

        retroBaseApiService.getUbinfo(useruid).enqueue(new Callback<List<UBookListItem>>() {
            @Override
            public void onResponse(Call<List<UBookListItem>> call, Response<List<UBookListItem>> response) {
                //소유 도서 정보 있음
                blist = response.body();

                items = new ArrayList<>();
                for(int i = 0; i < blist.size(); i++) {
                    String ubisbn = blist.get(i).getISBN13();
                    int ubookuid = blist.get(i).getUserBookUID();
                    String ubimg = blist.get(i).getBookImageUrl();
                    String ubname = blist.get(i).getBookName();
                    String ubpub = blist.get(i).getPublisher();

                    //저자 정보 가져오기
                    Retrofit retro_author = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_author.create(RetroBaseApiService.class);

                    retroBaseApiService.getAuthor(ubisbn).enqueue(new Callback<List<BookData>>() {
                        @Override
                        public void onResponse(Call<List<BookData>> call, Response<List<BookData>> response) {
                            //저자 정보 있음
                            authorDataList = response.body();
                            authorlist = authorDataList;

                            authorsam = new ArrayList<>();
                            for(int j = 0; j < authorlist.size(); j++){
                                authorsam.add(authorlist.get(j).getAuthor());
                            }
                            author = authorsam.toString();
                            author = String.join(" | ", authorsam);

                            //이미지 비트맵으로 변환
                            if(ubimg == null){
                                //기본 이미지 비트맵으로 변환
                                Bitmap bmm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_img);
                                int height = bmm.getHeight();
                                int width = bmm.getWidth();

                                Bitmap resized = null;
                                while(height>70){
                                    resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                    height = resized.getHeight();
                                    width = resized.getWidth();
                                }
                                bp = resized;
                                Log.d(TAG,"저자 정보 있음, 기본 이미지 : "+bp);
                                //items listview 넣기
                                ub = new UserBookItem(ubname, ubpub, bp, author, ubisbn, ubookuid);
                                items.add(ub);
                                initView();
                            } else {
                                //이미지 url 비트맵으로 변환
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            URL url = new URL(ubimg);
                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.connect();
                                            InputStream bis = conn.getInputStream();
                                            Bitmap bmm = BitmapFactory.decodeStream(bis);

                                            int height = bmm.getHeight();
                                            int width = bmm.getWidth();

                                            Bitmap resized = null;
                                            while(height>70){
                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                height = resized.getHeight();
                                                width = resized.getWidth();
                                            }
                                            bp = resized;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }); thread.start();
                                try {
                                    thread.join();
                                    Log.d(TAG,"저자 정보 있음, 도서 이미지 : "+bp);
                                    //items listview 넣기
                                    ub = new UserBookItem(ubname, ubpub, bp, author, ubisbn, ubookuid);
                                    items.add(ub);
                                    initView();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<List<BookData>> call, Throwable t) {
                            //저자 정보 없음
                            //이미지 비트맵으로 변환
                            if(ubimg == null){
                                //기본 이미지 비트맵으로 변환
                                Bitmap bmm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_img);
                                int height = bmm.getHeight();
                                int width = bmm.getWidth();

                                Bitmap resized = null;
                                while(height>70){
                                    resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                    height = resized.getHeight();
                                    width = resized.getWidth();
                                }
                                bp = resized;
                                Log.d(TAG,"저자 정보 없음, 기본 이미지 : "+bp);
                                //items listview 넣기
                                ub = new UserBookItem(ubname, ubpub, bp, " ",ubisbn, ubookuid);
                                items.add(ub);
                                initView();
                            } else {
                                //이미지 url 비트맵으로 변환
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            URL url = new URL(ubimg);
                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.connect();
                                            InputStream bis = conn.getInputStream();
                                            Bitmap bmm = BitmapFactory.decodeStream(bis);

                                            int height = bmm.getHeight();
                                            int width = bmm.getWidth();

                                            Bitmap resized = null;
                                            while(height>70){
                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                height = resized.getHeight();
                                                width = resized.getWidth();
                                            }
                                            bp = resized;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }); thread.start();
                                try {
                                    thread.join();
                                    Log.d(TAG,"저자 정보 없음, 도서 이미지 : "+bp);
                                    //items listview 넣기
                                    ub = new UserBookItem(ubname, ubpub, bp, " ",ubisbn, ubookuid);
                                    items.add(ub);
                                    initView();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<UBookListItem>> call, Throwable t) {

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private void initView() {
        MyBookAdapter myAdapter = new MyBookAdapter(getApplicationContext(),
                R.layout.listview_mybook, items);
        ListView listview = (ListView) findViewById(R.id.mb_list);
        Log.d(TAG,"검색 도서 정보 어댑터 선언");

        listview.setAdapter(myAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
