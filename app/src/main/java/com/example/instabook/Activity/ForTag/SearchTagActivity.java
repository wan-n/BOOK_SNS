package com.example.instabook.Activity.ForTag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.TagBookAdapter;
import com.example.instabook.ListView.TagBookItem;
import com.example.instabook.ListView.UserBookItem;
import com.example.instabook.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SearchTagActivity extends AppCompatActivity {
    private static final String TAG = "SearchTagActivity";
    public static RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;
    FrameLayout tag_fr_back;
    ImageView tag_back;
    List<TagData> ruidlist = new ArrayList<TagData>();
    List<TagData> taglist = new ArrayList<TagData>();
    List<TagData> blist = new ArrayList<TagData>();
    List<TagData> ubuidlist = new ArrayList<TagData>();
    List<TagData> authorlist = new ArrayList<TagData>();
    Bitmap bp;
    TagBookItem tb;
    TagData tagdata = new TagData();

    ArrayList<TagBookItem> items = new ArrayList<TagBookItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag);

        Intent intent = getIntent();
        String key = intent.getStringExtra("keyword");

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(SearchTagActivity.this);

        tag_back = findViewById(R.id.st_back);
        tag_fr_back = findViewById(R.id.st_fr_back);

        //뒤로가기 버튼
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.st_fr_back:
                        onBackPressed();
                        break;
                }
            }
        };
        tag_fr_back.setOnClickListener(listener);


        Log.d(TAG,"태그 검색어로 ReviewUID 가져오기");
        //태그 검색어로 ReviewUID 가져오기
        Retrofit retro_ruid = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_ruid.create(RetroBaseApiService.class);

        retroBaseApiService.getruidlist(key).enqueue(new Callback<List<TagData>>() {
            @Override
            public void onResponse(Call<List<TagData>> call, Response<List<TagData>> response) {
                Log.d(TAG,"리뷰 uid 받아옴");
                //리뷰 uid 받아옴
                ruidlist = response.body();
                ArrayList<Integer> ruids = new ArrayList<>();

                for(int i = 0; i < ruidlist.size(); i++){
                    ruids.add(ruidlist.get(i).getReviewUID());
                }


                //리뷰 uid로 리뷰 태그와 isbn 가져오기
                for(int i = 0; i < ruids.size(); i++){
                    int reviewuid = ruids.get(i);

                    Retrofit retro_tag = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_tag.create(RetroBaseApiService.class);

                    retroBaseApiService.getTags(reviewuid).enqueue(new Callback<List<TagData>>() {
                        @Override
                        public void onResponse(Call<List<TagData>> call, Response<List<TagData>> response) {
                            Log.d(TAG,"태그 정보 받아옴");
                            taglist = response.body();
                            final String isbn = taglist.get(0).getISBN13();
                            String[] tags = new String[taglist.size()];
                            String singletag = "";

                            for(int i = 0; i < taglist.size(); i++){
                                tags[i] = taglist.get(i).getTag();
                            }
                            for(int j =0; j<taglist.size(); j++){
                                singletag += "#" + taglist.get(j).getTag();
                            }

                            //isbn로 리뷰 도서 정보 가져오기
                            Retrofit retro_book = new Retrofit.Builder()
                                    .baseUrl(retroBaseApiService.Base_URL)
                                    .addConverterFactory(GsonConverterFactory.create()).build();
                            retroBaseApiService = retro_book.create(RetroBaseApiService.class);

                            String finalSingletag = singletag;
                            retroBaseApiService.getbooks(isbn).enqueue(new Callback<List<TagData>>() {
                                @Override
                                public void onResponse(Call<List<TagData>> call, Response<List<TagData>> response) {
                                    Log.d(TAG,"리뷰 도서 정보 받아옴");
                                    blist = response.body();

                                    for(int i = 0; i < blist.size(); i++){
                                        final String burl = blist.get(i).getBookImageUrl();
                                        final String bname = blist.get(i).getBookName();
                                        final String pub = blist.get(i).getPublisher();
                                        String pubdate1 = blist.get(i).getPublishDate();

                                        //날짜변환
                                        Date date = null;
                                        try {
                                            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(pubdate1);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                        String pubdate = sdf.format(date);


                                        //useruid와 isbn으로 찜 도서 uid 가져오기
                                        Retrofit retro_ubuid = new Retrofit.Builder()
                                                .baseUrl(retroBaseApiService.Base_URL)
                                                .addConverterFactory(GsonConverterFactory.create()).build();
                                        retroBaseApiService = retro_ubuid.create(RetroBaseApiService.class);

                                        retroBaseApiService.getUbuid(isbn,useruid).enqueue(new Callback<List<TagData>>() {
                                            @Override
                                            public void onResponse(Call<List<TagData>> call, Response<List<TagData>> response) {
                                                Log.d(TAG,"찜 도서 uid 받아옴");
                                                ubuidlist = response.body();
                                                final int ubuid = ubuidlist.get(0).getUserBookUID();


                                                //isbn으로 저자 정보 가져오기
                                                Retrofit retro_author = new Retrofit.Builder()
                                                        .baseUrl(retroBaseApiService.Base_URL)
                                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                                retroBaseApiService = retro_author.create(RetroBaseApiService.class);

                                                retroBaseApiService.getAuthort(isbn).enqueue(new Callback<List<TagData>>() {
                                                    @Override
                                                    public void onResponse(Call<List<TagData>> call, Response<List<TagData>> response) {
                                                        Log.d(TAG,"저자 정보 받아옴");
                                                        authorlist = response.body();

                                                        ArrayList<String> authorsam = new ArrayList<>();
                                                        for(int i = 0; i < authorlist.size(); i++){
                                                            authorsam.add(authorlist.get(i).getAuthor());
                                                        }
                                                        String author = String.join(" | ",authorsam);

                                                        //이미지 비트맵으로 변환
                                                        if(burl == null){
                                                            //기본 이미지 비트맵으로 변환
                                                            Bitmap bmm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_img);
                                                            int height = bmm.getHeight();
                                                            int width = bmm.getWidth();

                                                            Bitmap resized = null;
                                                            while(height>70){
                                                                resized = Bitmap.createScaledBitmap(bmm,70,70,true);
                                                                height = resized.getHeight();
                                                                width = resized.getWidth();
                                                            }
                                                            bp = resized;

                                                            //items listview 넣기
                                                            tb = new TagBookItem(reviewuid, finalSingletag, isbn, burl, bname, pub, pubdate, ubuid, author, bp);
                                                            items.add(tb);

                                                            initView();
                                                        } else {
                                                            //이미지 url 비트맵으로 변환
                                                            Thread thread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        URL url = new URL(burl);
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

                                                                //items listview 넣기
                                                                tb = new TagBookItem(reviewuid, finalSingletag, isbn, burl, bname, pub, pubdate, ubuid, author, bp);
                                                                items.add(tb);

                                                                initView();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<List<TagData>> call, Throwable t) {
                                                        Log.d(TAG,"저자 정보 없음");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<List<TagData>> call, Throwable t) {
                                                Log.d(TAG,"찜 도서 uid 없음");
                                                final int ubuid = 0;


                                                //isbn으로 저자 정보 가져오기
                                                Retrofit retro_author = new Retrofit.Builder()
                                                        .baseUrl(retroBaseApiService.Base_URL)
                                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                                retroBaseApiService = retro_author.create(RetroBaseApiService.class);

                                                retroBaseApiService.getAuthort(isbn).enqueue(new Callback<List<TagData>>() {
                                                    @Override
                                                    public void onResponse(Call<List<TagData>> call, Response<List<TagData>> response) {
                                                        Log.d(TAG,"저자 정보 받아옴");
                                                        authorlist = response.body();

                                                        ArrayList<String> authorsam = new ArrayList<>();
                                                        for(int i = 0; i < authorlist.size(); i++){
                                                            authorsam.add(authorlist.get(i).getAuthor());
                                                        }
                                                        String author = String.join(" | ",authorsam);

                                                        //이미지 비트맵으로 변환
                                                        if(burl == null){
                                                            //기본 이미지 비트맵으로 변환
                                                            Bitmap bmm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_img);
                                                            int height = bmm.getHeight();
                                                            int width = bmm.getWidth();

                                                            Bitmap resized = null;
                                                            while(height>70){
                                                                resized = Bitmap.createScaledBitmap(bmm,70,70,true);
                                                                height = resized.getHeight();
                                                                width = resized.getWidth();
                                                            }
                                                            bp = resized;

                                                            //items listview 넣기
                                                            tb = new TagBookItem(reviewuid, finalSingletag, isbn, burl, bname, pub, pubdate, ubuid, author, bp);
                                                            items.add(tb);

                                                            initView();
                                                        } else {
                                                            //이미지 url 비트맵으로 변환
                                                            Thread thread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        URL url = new URL(burl);
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

                                                                //items listview 넣기
                                                                tb = new TagBookItem(reviewuid, finalSingletag, isbn, burl, bname, pub, pubdate, ubuid, author, bp);
                                                                items.add(tb);

                                                                initView();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<List<TagData>> call, Throwable t) {
                                                        Log.d(TAG,"저자 정보 없음");
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<TagData>> call, Throwable t) {
                                    Log.d(TAG, "리뷰 도서 정보 없음");
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<List<TagData>> call, Throwable t) {
                            Log.d(TAG,"태그 정보 없음");
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<TagData>> call, Throwable t) {
                Log.d(TAG,"리뷰 uid 없음");
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
         TagBookAdapter tbAdapter = new TagBookAdapter(this, R.layout.listview_tagbook, items);
        ListView listview = (ListView) findViewById(R.id.st_list);
        Log.d(TAG,"태그 도서 정보 어댑터 선언");

        listview.setAdapter(tbAdapter);

        /*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                TagBookItem tagBookItem = (TagBookItem) listView.getItemAtPosition(position);
                //TagBookItem tagBookItem = items.get(position);

                Bitmap bm = tagBookItem.getBp();
                String title = tagBookItem.getBname();
                String author = tagBookItem.getAuthor();
                String isbn = tagBookItem.getIsbn();
                String pub = tagBookItem.getPub();
                String pubdate = tagBookItem.getPubdate();
                String tag = tagBookItem.getTag();
                int ubuid = tagBookItem.getUbuid();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] b = stream.toByteArray();

                Intent intent = new Intent(SearchTagActivity.this, BookInfoActivity.class);
                intent.putExtra("bm",b);
                intent.putExtra("title",title);
                intent.putExtra("author",author);
                intent.putExtra("isbn",isbn);
                intent.putExtra("pub",pub);
                intent.putExtra("pubdate",pubdate);
                intent.putExtra("tag",tag);
                intent.putExtra("ubuid",ubuid);
                startActivity(intent);
            }
        });

        listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                TagBookItem tagBookItem = (TagBookItem) listView.getItemAtPosition(position);
                //TagBookItem tagBookItem = items.get(position);

                Bitmap bm = tagBookItem.getBp();
                String title = tagBookItem.getBname();
                String author = tagBookItem.getAuthor();
                String isbn = tagBookItem.getIsbn();
                String pub = tagBookItem.getPub();
                String pubdate = tagBookItem.getPubdate();
                String tag = tagBookItem.getTag();
                int ubuid = tagBookItem.getUbuid();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] b = stream.toByteArray();

                Intent intent = new Intent(SearchTagActivity.this, BookInfoActivity.class);
                intent.putExtra("bm",b);
                intent.putExtra("title",title);
                intent.putExtra("author",author);
                intent.putExtra("isbn",isbn);
                intent.putExtra("pub",pub);
                intent.putExtra("pubdate",pubdate);
                intent.putExtra("tag",tag);
                intent.putExtra("ubuid",ubuid);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tbAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}