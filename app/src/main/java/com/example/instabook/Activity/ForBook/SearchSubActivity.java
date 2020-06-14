package com.example.instabook.Activity.ForBook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.net.wifi.WifiConfiguration.Status.strings;
import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class SearchSubActivity extends AppCompatActivity {
    private static final String TAG = "SearchSubActivity";
    static List<NaverBookData> list; //getNaverSearch 함수에서 return된 도서 정보 리스트

    ArrayList<SearchBookItem> items;
    SearchBookItem sb;
    String keyword;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_searchsub);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = getIntent(); //데이터 수신
                    keyword = intent.getStringExtra("keyword"); //intent 값을 String 타입으로 변환

                    getNaverSearch(keyword);
                    HashMap<String, Object> map = new HashMap<>();
                    items = new ArrayList<>();
                    for(int i = 0; i <list.size(); i++){
                        String bname = list.get(i).getTitle();
                        String isbn = list.get(i).getIsbn();
                        String pub = list.get(i).getPublisher();
                        String pdate = list.get(i).getPubdate();
                        String price = list.get(i).getPrice();
                        String sale = list.get(i).getDiscount();
                        String img = list.get(i).getImage();

                        map.put("bookname", bname);
                        map.put("isbn",isbn);
                        map.put("pub",pub);
                        map.put("pdate",pdate);
                        map.put("price",price);
                        map.put("sale",sale);
                        map.put("img",img);

                        sb = new SearchBookItem(bname, isbn, pub);
                        items.add(sb);
                    }

                    Retrofit naver_retro = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = naver_retro.create(RetroBaseApiService.class);

                    retroBaseApiService.postNbook(map).enqueue(new Callback<NaverData>() {
                        @Override
                        public void onResponse(Call<NaverData> call, Response<NaverData> response) {
                            Toast.makeText(getApplicationContext(), "네이버 도서로 검색", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<NaverData> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "네이버 도서 검색 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BookListAdapter blAdapter = new BookListAdapter(getApplicationContext(),
                                    R.layout.listview_searchbook, items);

                            ListView listview = (ListView) findViewById(R.id.listview);
                            listview.setAdapter(blAdapter);
                            /*
                            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });
                            */
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }); thread.start();
    }


     public static List<NaverBookData> getNaverSearch(final String keyword) {
         final String clientId = "q9rD74qm2NEUevZOZ0HA";
         final String clientSecret = "KVrwN1NIGi";
         int display = 10;

         new Thread() {
            @Override
            public void run() {
                //결과데이터 담을 리스트
                List<NaverBookData> booklist = null;
                NaverBookData bookdata = null;
                try {
                    String text = URLEncoder.encode(keyword, "UTF-8"); //파싱할 url 지정
                    String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + text + "&start=1" + "&display=" + display +"&";

                    URL url = new URL(apiURL);
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser(); //연결 담기

                    String tag;
                    xpp.setInput(new InputStreamReader(con.getInputStream(),"UTF-8"));

                    xpp.next();
                    int eventType = xpp.getEventType(); //파싱 시작
                    Log.d(TAG,"while문 들어가기 전 EventType"+eventType);

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        tag = xpp.getName(); //태그 이름 얻어오기, 저장한 tag값을 확인하여 적절한 변수에 값을 넣아야함
                        Log.d(TAG,"while문 안 쪽 tag 명"+tag);
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                booklist = new ArrayList<>();
                                break;
                            case XmlPullParser.START_TAG: {//START_TAG : 태그의 시작, 시작 태그를 만나면 이름을 봐서 저장한다.
                                if(tag.equals("item")){
                                    bookdata = new NaverBookData();
                                }
                                else if(tag.equals("title")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                        Log.d(TAG,"타이틀 title: "+bookdata.getTitle());
                                    }
                                }
                                else if(tag.equals("link")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                        Log.d(TAG,"링크 LINK: "+bookdata.getLink());
                                    }
                                }
                                else if(tag.equals("image")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                else if(tag.equals("author")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                else if(tag.equals("price")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                else if(tag.equals("discount")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                else if(tag.equals("publisher")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                else if(tag.equals("pubdate")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                else if(tag.equals("isbn")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                else if(tag.equals("description")){
                                    if (bookdata != null) {
                                        bookdata.setTitle(xpp.nextText());
                                    }
                                }
                                break;
                            }
                            case XmlPullParser.END_TAG: {//End 태그를 만나면
                                if (tag.equalsIgnoreCase("item") && bookdata != null) {
                                    if (booklist != null) {
                                    booklist.add(bookdata);
                                    bookdata = null;
                                    }
                                }
                                break;
                            }
                        }
                        Log.d(TAG, "size잘나오니: " + booklist.size());
                        Log.d(TAG, "title잘나오니: " + booklist.get(0).getTitle());
                        eventType = xpp.next();
                    }
                } catch (Exception e) {

                }
                list = booklist;
            }
         }.start();
         return list;
    }
}
