package com.example.instabook.Activity.ForBook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Adapter.BookListAdapter;
import com.example.instabook.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchSubActivity extends AppCompatActivity {
    ArrayList<BookData> booklist = null;
    BookListAdapter bladapter;
    ListView listview;
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

                    //getNaverSearch(keyword);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listview = (ListView) findViewById(R.id.listview);
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

    /**
    public void getNaverSearch(String keyword) {
        final String clientId = "q9rD74qm2NEUevZOZ0HA";
        final String clientSecret = "KVrwN1NIGi";
        int display = 10;

        new Thread() {
            @Override
            public void run() {
                String tag = null;
                try {
                    String text = URLEncoder.encode(keyword, "UTF-8"); //파싱할 url 지정
                    String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + text
                            + "&start=1" + "&display=" + display;

                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String data="";
                    String msg = null;
                    while((msg = br.readLine())!=null)
                    {
                        data += msg;
                    }

                    //결과데이터 담을 리스트
                    ArrayList<BookData> booklist = null;
                    BookData bookdata = null;

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser(); //연결 담기

                    xpp.setInput(new StringReader(data));
                    int eventType = xpp.getEventType(); //파싱 시작

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.END_DOCUMENT:
                                break;//END_DOCUMENT : 문서의 끝

                            case XmlPullParser.START_DOCUMENT:
                                booklist = new ArrayList<>();
                                break;


                            case XmlPullParser.START_TAG: {//START_TAG : 태그의 시작, 시작 태그를 만나면 이름을 봐서 저장한다.
                                tag = xpp.getName(); //태그 이름 얻어오기, 저장한 tag값을 확인하여 적절한 변수에 값을 넣아야함
                                switch (tag) {
                                    case "item":
                                        bookdata = new BookData();
                                        break;
                                    case "title":
                                        if (bookdata != null)
                                            bookdata.setTitle(xpp.nextText());
                                        break;
                                    case "link":
                                        if (bookdata != null)
                                            bookdata.setLink(xpp.nextText());
                                        break;
                                    case "image":
                                        if (bookdata != null)
                                            bookdata.setImag(xpp.nextText());
                                        break;
                                    case "author":
                                        if (bookdata != null)
                                            bookdata.setAuthor(xpp.nextText());
                                        break;
                                    case "price":
                                        if (bookdata != null)
                                            bookdata.setPrice(xpp.nextText());
                                        break;
                                    case "discount":
                                        if (bookdata != null)
                                            bookdata.setDiscount(xpp.nextText());
                                        break;
                                    case "publisher":
                                        if (bookdata != null)
                                            bookdata.setPublisher(xpp.nextText());
                                        break;
                                    case "pubdate":
                                        if (bookdata != null)
                                            bookdata.setPubdate(xpp.nextText());
                                        break;
                                    case "isbn":
                                        if (bookdata != null)
                                            bookdata.setIsbn(xpp.nextText());
                                        break;
                                    case "description":
                                        if (bookdata != null)
                                            bookdata.setDescription(xpp.nextText());
                                        break;
                                }
                                break;
                            }

                            case XmlPullParser.END_TAG: {//End 태그를 만나면
                                tag = xpp.getName(); //태그 이름 얻어오기
                                if (tag.equalsIgnoreCase("item") && bookdata != null) {
                                    if (booklist != null) {
                                        booklist.add(bookdata);
                                        bookdata = null;
                                    }
                                }
                                break;
                            }
                        }
                        Log.d(tag, "size잘나오니: " + booklist.size());
                        Log.d(tag, "title잘나오니: " + booklist.get(1).getTitle());
                        eventType = xpp.next();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewDataAdd();
                        }
                    });



                } catch (Exception e) {
                    Log.d(tag, "error : " + e);
                }
            }
        }.start();
    }

    public void listViewDataAdd(){
        bladapter = new BookListAdapter();

        for (int i = 0; i < booklist.size(); i++) {
            String t = booklist.get(i).getTitle();
            String a = booklist.get(i).getAuthor();
            String p = booklist.get(i).getPublisher();

            bladapter.addItem(t,a,p);
        }

        listview.setAdapter(bladapter);
    }
     */
}
