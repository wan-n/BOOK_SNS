package com.example.instabook.Activity.ForBook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Adapter.BookListAdapter;
import com.example.instabook.ListView.SearchBookItem;
import com.example.instabook.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchdbActivity extends AppCompatActivity {
    private static final String TAG = "SearchdbActivity";
    /**검색어*/
    String keyword;
    /**DB 도서 정보 변수*/
    ArrayList<SearchBookItem> books;
    List<BookData> bookDataList = new ArrayList<BookData>();
    List<BookData> authorDataList;
    List<BookData> authorlist;
    List<String> authorsam;
    String author = "";
    SearchBookItem mb;
    /**Naver 도서 정보 변수*/
    List<NaverBookData> nblist = new ArrayList<NaverBookData>();
    SearchBookItem sb;
    ArrayList<SearchBookItem> items;
    RetroBaseApiService retroBaseApiService;

    Bitmap bm = null;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchsub);

        Intent intent = getIntent(); //데이터 수신
        keyword = intent.getStringExtra("keyword"); //intent 값을 String 타입으로 변환
        Log.d(TAG, "키워드: " + keyword);

        getBook(keyword);
    }

    //DB에서 도서 정보 가져오기
    public void getBook(String keyword){
        context = getApplicationContext();

        Retrofit retro_book = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_book.create(RetroBaseApiService.class);

        retroBaseApiService.getBook(keyword).enqueue(new Callback<List<BookData>>() {
            @Override
            public void onResponse(Call<List<BookData>> call, Response<List<BookData>> response) {
                bookDataList = response.body();
                books = new ArrayList<>();

                for(int i = 0; i < bookDataList.size(); i++){
                    String b = bookDataList.get(i).getBookName();
                    String p = bookDataList.get(i).getPublisher();
                    String is = bookDataList.get(i).getISBN13();
                    String url = bookDataList.get(i).getBookImageUrl();

                    //저자 정보 가져오기
                    Retrofit retro_author = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_author.create(RetroBaseApiService.class);

                    retroBaseApiService.getAuthor(is).enqueue(new Callback<List<BookData>>() {
                        @Override
                        public void onResponse(Call<List<BookData>> call, Response<List<BookData>> response) {
                            authorDataList = response.body();
                            authorlist = authorDataList;
                            authorsam = new ArrayList<>();

                            for(int j = 0; j < authorlist.size(); j++){
                                authorsam.add(authorlist.get(j).getAuthor());
                            }
                            author = authorsam.toString();
                            author = String.join(" | ", authorsam);

                            if(url == null){
                                //기본 이미지 비트맵으로 변환
                                Bitmap bmm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
                                /*
                                int height = bmm.getHeight();
                                int width = bmm.getWidth();

                                Bitmap resized = null;
                                if(height>width){
                                    while(height>70){
                                        resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                        height = resized.getHeight();
                                        width = resized.getWidth();
                                    }
                                }else {
                                    while(width>70){
                                        resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                        height = resized.getHeight();
                                        width = resized.getWidth();
                                    }
                                }
                                bm = resized;*/
                                mb = new SearchBookItem(b, author, p, url, is, bm);
                                books.add(mb);
                                initView();
                            } else {
                                //이미지 url 비트맵으로 변환
                                String imgurl = url;
                                Thread bthread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            URL url = new URL(imgurl);
                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.connect();
                                            InputStream bis = conn.getInputStream();
                                            Bitmap bmm = BitmapFactory.decodeStream(bis);
                                            /*
                                            int height = bmm.getHeight();
                                            int width = bmm.getWidth();

                                            Bitmap resized = null;

                                            if(height>width) {
                                                while (height > 70) {
                                                    resized = Bitmap.createScaledBitmap(bmm, (width * 70) / height, 70, true);
                                                    height = resized.getHeight();
                                                    width = resized.getWidth();
                                                }
                                            } else {
                                                while(width>70){
                                                    resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                    height = resized.getHeight();
                                                    width = resized.getWidth();
                                                }
                                            }
                                            bm = resized;*/
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }); bthread.start();
                                try {
                                    bthread.join();
                                    mb = new SearchBookItem(b, author, p, url, is, bm);
                                    books.add(mb);
                                    initView();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<BookData>> call, Throwable t) {
                            //저자 정보 없음
                            if(url == null){
                                //기본 이미지 비트맵으로 변환
                                Bitmap bmm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
                                /*
                                int height = bmm.getHeight();
                                int width = bmm.getWidth();

                                Bitmap resized = null;
                                if(height>width) {
                                    while (height > 70) {
                                        resized = Bitmap.createScaledBitmap(bmm, (width * 70) / height, 70, true);
                                        height = resized.getHeight();
                                        width = resized.getWidth();
                                    }
                                } else {
                                    while(width>70){
                                        resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                        height = resized.getHeight();
                                        width = resized.getWidth();
                                    }
                                }
                                bm = resized;*/
                                mb = new SearchBookItem(b, " ", p, url, is, bm);
                                books.add(mb);
                                initView();
                            } else {
                                //이미지 url 비트맵으로 변환
                                String imgurl = url;
                                Thread bthread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            URL url = new URL(imgurl);
                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.connect();
                                            InputStream bis = conn.getInputStream();
                                            Bitmap bmm = BitmapFactory.decodeStream(bis);
                                            /*
                                            int height = bmm.getHeight();
                                            int width = bmm.getWidth();

                                            Bitmap resized = null;
                                            if(height>width) {
                                                while (height > 70) {
                                                    resized = Bitmap.createScaledBitmap(bmm, (width * 70) / height, 70, true);
                                                    height = resized.getHeight();
                                                    width = resized.getWidth();
                                                }
                                            }
                                            else {
                                                while(width>70){
                                                    resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                    height = resized.getHeight();
                                                    width = resized.getWidth();
                                                }
                                            }
                                            bm = resized;*/
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }); bthread.start();
                                try {
                                    bthread.join();
                                    mb = new SearchBookItem(b, " ", p, url, is, bm);
                                    books.add(mb);
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
            public void onFailure(Call<List<BookData>> call, Throwable t) {
                Log.d(TAG,"네이버 도서 정보 가져오기");
                getNaverSearch(keyword);
            }
        });
    }

    private void initView() {
        BookListAdapter bAdapter = new BookListAdapter(getApplicationContext(),
                R.layout.listview_searchbook, books);
        ListView listview = (ListView) findViewById(R.id.sb_listview);
        Log.d(TAG,"검색 도서 정보 어댑터 선언");

        listview.setAdapter(bAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //bAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    boolean initem = false, intitle = false, inlink = false, inimage = false, inauthor = false, inprice = false, indiscount = false,
            inpublisher = false, inpubdate = false, inisbn = false, indescription = false;

    public void getNaverSearch(final String keyword) {
        Log.d(TAG, "위치 getNaverSearch");

        final String clientId = "q9rD74qm2NEUevZOZ0HA";
        final String clientSecret = "KVrwN1NIGi";
        int display = 20;

        Thread nbthread = new Thread(){
            @Override
            public void run() {
                try {
                    String text = URLEncoder.encode(keyword, "UTF-8");
                    Log.d(TAG, "네이버 검색 안 검색어 : " + keyword);
                    Log.d(TAG, "네이버 검색 안 검색어 : " + text);
                    String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + text + "&display=" + display;

                    Map<String, String> requestHeaders = new HashMap<>();
                    requestHeaders.put("X-Naver-Client-Id", clientId);
                    requestHeaders.put("X-Naver-Client-Secret", clientSecret);

                    String responseBody = get(apiURL, requestHeaders);
                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = parserCreator.newPullParser();
                    xpp.setInput(new StringReader(responseBody));

                    String tag = null;
                    int eventType = 0; //파싱 시작
                    eventType = xpp.getEventType();

                    //결과데이터 담을 리스트
                    List<NaverBookData> booklist = new ArrayList<>();
                    NaverBookData bookdata = null;

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                booklist = new ArrayList<NaverBookData>();
                                break;
                            case XmlPullParser.START_TAG: {//START_TAG : 태그의 시작, 시작 태그를 만나면 이름을 봐서 저장한다.
                                tag = xpp.getName(); //태그 이름 얻어오기, 저장한 tag값을 확인하여 적절한 변수에 값을 넣아야함
                                if (tag.equals("item")) {
                                    bookdata = new NaverBookData();
                                    initem = true;
                                } else if (tag.equals("title")) {
                                    intitle = true;
                                } else if (tag.equals("link")) {
                                    inlink = true;
                                } else if (tag.equals("image")) {
                                    inimage = true;
                                } else if (tag.equals("author")) {
                                    inauthor = true;
                                } else if (tag.equals("price")) {
                                    inprice = true;
                                } else if (tag.equals("discount")) {
                                    indiscount = true;
                                } else if (tag.equals("publisher")) {
                                    inpublisher = true;
                                } else if (tag.equals("pubdate")) {
                                    inpubdate = true;
                                } else if (tag.equals("isbn")) {
                                    inisbn = true;
                                } else if (tag.equals("description")) {
                                    indescription = true;
                                }
                                break;
                            }
                            case XmlPullParser.TEXT: {
                                if (intitle) {
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.getText().replaceAll("\\<.*?>", ""));
                                    }
                                    intitle = false;
                                }
                                if (inlink) {
                                    if (bookdata != null) {
                                        bookdata.setLink(xpp.getText());
                                    }
                                    inlink = false;
                                }
                                if (inimage) {
                                    if (bookdata != null) {
                                        bookdata.setImage(xpp.getText());
                                    }
                                    inimage = false;
                                }
                                if (inauthor) {
                                    if (bookdata != null) {
                                        bookdata.setAuthor(xpp.getText().replaceAll("\\<.*?>", ""));
                                    }
                                    inauthor = false;
                                }
                                if (inprice) {
                                    if (bookdata != null) {
                                        bookdata.setPrice(xpp.getText());
                                    }
                                    inprice = false;
                                }
                                if (indiscount) {
                                    if (bookdata != null) {
                                        bookdata.setDiscount(xpp.getText());
                                    }
                                    indiscount = false;
                                }
                                if (inpublisher) {
                                    if (bookdata != null) {
                                        bookdata.setPublisher(xpp.getText().replaceAll("\\<.*?>", ""));
                                    }
                                    inpublisher = false;
                                }
                                if (inpubdate) {
                                    if (bookdata != null) {
                                        bookdata.setPubdate(xpp.getText());
                                    }
                                    inpubdate = false;
                                }
                                if (inisbn) {
                                    if (bookdata != null) {
                                        bookdata.setIsbn(xpp.getText());
                                    }
                                    inisbn = false;
                                }
                                if (indescription) {
                                    if (bookdata != null) {
                                        bookdata.setDescription(xpp.getText().replaceAll("\\<.*?>", ""));
                                    }
                                    indescription = false;
                                }
                                break;
                            }
                            case XmlPullParser.END_TAG: { //End 태그를 만나면
                                String endtag = xpp.getName();
                                if (endtag.equals("item")) {
                                    booklist.add(bookdata);
                                    bookdata = null;
                                    initem = false;
                                }
                                break;
                            }
                            case XmlPullParser.END_DOCUMENT:
                                break;
                        }
                        eventType = xpp.next();
                        nblist = booklist;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        nbthread.start();
        try{
            nbthread.join();
            Log.d(TAG, "join 후 setNaverSearch");
            Log.d(TAG,"네이버 도서 : "+nblist.get(0).getTitle()+", "+nblist.get(0).getAuthor()+", "+nblist.get(0).getIsbn()+", "+
                            nblist.get(0).getPublisher()+", "+nblist.get(0).getPubdate()+", "+nblist.get(0).getDiscount()+", "+
                            nblist.get(0).getPrice()+", "+nblist.get(0).getImage());
            Log.d(TAG,"네이버 도서 : "+nblist.get(1).getTitle()+", "+nblist.get(1).getAuthor()+", "+nblist.get(1).getIsbn()+", "+
                    nblist.get(1).getPublisher()+", "+nblist.get(1).getPubdate()+", "+nblist.get(1).getDiscount()+", "+
                    nblist.get(1).getPrice()+", "+nblist.get(1).getImage());
            setNaverBook();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setNaverBook(){
        Log.d(TAG, "위치 setNaverSearch");
        HashMap<String, Object> map = new HashMap<>();

        books = new ArrayList<>();
        for(int i = 0; i <nblist.size(); i++) {
            String bname = nblist.get(i).getTitle();
            String isbn26 = nblist.get(i).getIsbn();
            String pub = nblist.get(i).getPublisher();
            String pdate = nblist.get(i).getPubdate();
            String price = nblist.get(i).getPrice();
            int sale = 0;
            String burl = nblist.get(i).getImage();
            String author = nblist.get(i).getAuthor();

            //데이트 타임, isbn 변환, sale null 검사
            pdate += " 00:00:00.000";
            String isbn = isbn26.substring(isbn26.length()-13, isbn26.length());

            map.put("bookname", bname);
            map.put("isbn", isbn);
            map.put("pub", pub);
            map.put("pdate", pdate);
            map.put("price", price);
            map.put("sale", sale);
            map.put("img", burl);
            map.put("author",author);
            Log.d(TAG,"setNaverBook 함수 for문");

            //DB에 도서 정보 저장
            Retrofit naver_retro = new Retrofit.Builder()
                    .baseUrl(retroBaseApiService.Base_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            retroBaseApiService = naver_retro.create(RetroBaseApiService.class);

            retroBaseApiService.postNbook(map).enqueue(new Callback<NaverData>() {
                @Override
                public void onResponse(Call<NaverData> call, Response<NaverData> response) {
                    Log.d(TAG, "DB 저장 성공");

                    if(burl == null){
                        //기본 이미지 비트맵으로 변환
                        Bitmap bmm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
                        /*
                        int height = bmm.getHeight();
                        int width = bmm.getWidth();

                        Bitmap resized = null;
                        if(height>width) {
                            while (height > 70) {
                                resized = Bitmap.createScaledBitmap(bmm, (width * 70) / height, 70, true);
                                height = resized.getHeight();
                                width = resized.getWidth();
                            }
                        } else {
                            while(width>70){
                                resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                height = resized.getHeight();
                                width = resized.getWidth();
                            }
                        }

                        bm = resized;*/

                        sb = new SearchBookItem(bname, author, pub, burl, isbn , bm);
                        books.add(sb);
                        Log.d(TAG,"setNaverBook 함수 기본 이미지 books 추가");
                        initView();
                    } else {
                        Thread bthread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(burl);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.connect();
                                    InputStream bis = conn.getInputStream();
                                    Bitmap bmm = BitmapFactory.decodeStream(bis);
                                    /*
                                    int height = bmm.getHeight();
                                    int width = bmm.getWidth();

                                    Bitmap resized = null;
                                    if(height>width){
                                        while(height>70){
                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                            height = resized.getHeight();
                                            width = resized.getWidth();
                                        }
                                    } else {
                                        while(width>70){
                                            resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                            height = resized.getHeight();
                                            width = resized.getWidth();
                                        }
                                    }

                                    bm = resized;*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }); bthread.start();
                        try {
                            bthread.join();
                            sb = new SearchBookItem(bname, author, pub, burl, isbn ,bm);
                            books.add(sb);
                            Log.d(TAG,"setNaverBook 함수 도서 이미지 books 추가");
                            initView();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call<NaverData> call, Throwable t) {
                    Log.d(TAG, "DB 저장 실패");
                }
            });


        }
        Toast.makeText(getBaseContext(), "네이버 도서로 검색", Toast.LENGTH_SHORT).show();
    }


    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}