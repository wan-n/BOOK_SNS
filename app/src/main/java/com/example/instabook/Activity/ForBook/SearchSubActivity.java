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

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class SearchSubActivity extends AppCompatActivity {
    private static final String TAG = "SearchSubActivity";
    static List<NaverBookData> list;

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
                    for(int i = 0; i <list.size(); i++) {
                        String bname = list.get(i).getTitle();
                        String isbn = list.get(i).getIsbn();
                        String pub = list.get(i).getPublisher();
                        String pdate = list.get(i).getPubdate();
                        String price = list.get(i).getPrice();
                        String sale = list.get(i).getDiscount();
                        String img = list.get(i).getImage();

                        pdate += " 00:00:00.000";


                        map.put("bookname", bname);
                        map.put("isbn", isbn);
                        map.put("pub", pub);
                        map.put("pdate", pdate);
                        map.put("price", price);
                        map.put("sale", sale);
                        map.put("img", img);

                        sb = new SearchBookItem(bname, isbn, pub);
                        items.add(sb);

                        Retrofit naver_retro = new Retrofit.Builder()
                                .baseUrl(retroBaseApiService.Base_URL)
                                .addConverterFactory(GsonConverterFactory.create()).build();
                        retroBaseApiService = naver_retro.create(RetroBaseApiService.class);

                        retroBaseApiService.postNbook(map).enqueue(new Callback<NaverData>() {
                            @Override
                            public void onResponse(Call<NaverData> call, Response<NaverData> response) {

                            }

                            @Override
                            public void onFailure(Call<NaverData> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "네이버 도서 검색 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BookListAdapter blAdapter = new BookListAdapter(getApplicationContext(),
                                    R.layout.listview_searchbook, items);

                            ListView listview = (ListView) findViewById(R.id.listview);
                            listview.setAdapter(blAdapter);

                            Toast.makeText(getApplicationContext(), "네이버 도서로 검색", Toast.LENGTH_SHORT).show();
                            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }); thread.start();
    }


     public void getNaverSearch(final String keyword) {
         final String clientId = "q9rD74qm2NEUevZOZ0HA";
         final String clientSecret = "KVrwN1NIGi";

         int display = 100;
         boolean initem = false, intitle = false, inlink = false, inimage = false, inauthor = false, inprice = false, indiscount = false,
                 inpublisher = false, inpubdate = false, inisbn = false, indescription = false;

         String text = null;
         try {
             text = URLEncoder.encode(keyword, "UTF-8");
         } catch (UnsupportedEncodingException e) {
             throw new RuntimeException("검색어 인코딩 실패",e);
         }
         String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + text + "&display=" + display;

         Map<String, String> requestHeaders = new HashMap<>();
         requestHeaders.put("X-Naver-Client-Id", clientId);
         requestHeaders.put("X-Naver-Client-Secret", clientSecret);
         String responseBody = get(apiURL,requestHeaders);

         try {
             XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
             XmlPullParser xpp = parserCreator.newPullParser();
             xpp.setInput(new StringReader(responseBody));

             String tag = null;

             int eventType = 0; //파싱 시작
             eventType = xpp.getEventType();
             //결과데이터 담을 리스트
             List<NaverBookData> booklist = null;
             NaverBookData bookdata = null;

             while (eventType != XmlPullParser.END_DOCUMENT) {
                 switch (eventType) {
                     case XmlPullParser.START_DOCUMENT:
                         booklist = new ArrayList<NaverBookData>();
                         Log.d(TAG, "xml 시작");
                         break;

                     case XmlPullParser.START_TAG: //START_TAG : 태그의 시작, 시작 태그를 만나면 이름을 봐서 저장한다.
                         tag = xpp.getName(); //태그 이름 얻어오기, 저장한 tag값을 확인하여 적절한 변수에 값을 넣아야함
                         if(tag.equals("item")){
                             bookdata = new NaverBookData();
                             initem = true;
                         }
                         else if(tag.equals("title")){
                             intitle = true;
                         }
                         else if(tag.equals("link")){
                             inlink = true;
                         }
                         else if(tag.equals("image")){
                             inimage = true;
                         }
                         else if(tag.equals("author")){
                             inauthor = true;
                         }
                         else if(tag.equals("price")){
                             inprice = true;
                         }
                         else if(tag.equals("discount")){
                             indiscount = true;
                         }
                         else if(tag.equals("publisher")){
                             inpublisher = true;
                         }
                         else if(tag.equals("pubdate")){
                             inpubdate = true;
                         }
                         else if(tag.equals("isbn")){
                             inisbn = true;
                         }
                         else if(tag.equals("description")){
                             indescription = true;
                         }
                         break;

                     case XmlPullParser.TEXT:
                         if(intitle){
                             if (bookdata != null) {
                                 bookdata.setTitle(xpp.getText(). replaceAll("\\<.*?>","") );
                             }
                             intitle = false;
                         }
                         if(inlink){
                             if (bookdata != null) {
                                 bookdata.setLink(xpp.getText());
                             }
                             inlink = false;
                         }
                         if(inimage) {
                             if (bookdata != null) {
                                 bookdata.setImage(xpp.getText());
                             }
                             inimage = false;
                         }
                         if(inauthor){
                             if (bookdata != null) {
                                 bookdata.setAuthor(xpp.getText());
                             }
                             inauthor = false;
                         }
                         if(inprice){
                             if (bookdata != null) {
                                 bookdata.setPrice(xpp.getText());
                             }
                             inprice = false;
                         }
                         if(indiscount){
                             if (bookdata != null) {
                                 bookdata.setDiscount(xpp.getText());
                             }
                             indiscount = false;
                         }
                         if(inpublisher){
                             if (bookdata != null) {
                                 bookdata.setPublisher(xpp.getText());
                             }
                             inpublisher = false;
                         }
                         if(inpubdate){
                             if (bookdata != null) {
                                 bookdata.setPubdate(xpp.getText());
                             }
                             inpubdate = false;
                         }
                         if(inisbn){
                             if (bookdata != null) {
                                 bookdata.setIsbn(xpp.getText());
                             }
                             inisbn = false;
                         }
                         if(indescription){
                             if (bookdata != null) {
                                 bookdata.setDescription(xpp.getText(). replaceAll("\\<.*?>","") );
                             }
                             indescription = false;
                         }
                         break;

                     case XmlPullParser.END_TAG: //End 태그를 만나면
                         String endtag = xpp.getName();
                         if (endtag.equals("item")) {
                             booklist.add(bookdata);
                             bookdata = null;
                             initem = false;
                         }
                         break;
                     case XmlPullParser.END_DOCUMENT:
                         break;
                 }
                 eventType = xpp.next();
             }
             list = booklist;
             Log.d(TAG,"파싱끝");
         } catch (Exception e){
             e.printStackTrace();
         }
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
