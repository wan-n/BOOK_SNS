package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instabook.Activity.ForBook.BookData;
import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.SearchBookItem;
import com.example.instabook.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.instabook.Activity.ForReview.ModiReviewActivity.retroBaseApiService;

public class BookListAdapter extends BaseAdapter {
    private static final String TAG = "BookListAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    Bitmap bm;
    String imgurl;
    ArrayList<SearchBookItem> books;

    // ListViewAdapter의 생성자
    public BookListAdapter(Context context, int layout, ArrayList<SearchBookItem> books) {
        this.context = context;
        this.books = books;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return books.size();
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public SearchBookItem getItem(int position) {
        return books.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_searchbook, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView bookImageView = (ImageView) convertView.findViewById(R.id.img_book) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.text_title) ;
        TextView authorTextView = (TextView) convertView.findViewById(R.id.text_author) ;
        TextView pubTextView = (TextView) convertView.findViewById(R.id.text_pub) ;
        Button btn = (Button)convertView.findViewById(R.id.pick_btn);

        SearchBookItem searchBookItem = getItem(pos);
        String imgurll = searchBookItem.getImg();

        //이미지 가져오기
        if(imgurll == null){
            bookImageView.setImageResource(R.drawable.default_img);
        } else {
            int idx = imgurll.indexOf("?");
            imgurl = imgurll.substring(0, idx);

            Thread uthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(imgurl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.connect();
                        InputStream bis = conn.getInputStream();
                        bm = BitmapFactory.decodeStream(bis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            uthread.start();

            try{
                uthread.join();

                bookImageView.setImageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        titleTextView.setText(searchBookItem.getTitle());
        authorTextView.setText(searchBookItem.getAuthor());
        pubTextView.setText(searchBookItem.getPublisher());

        btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                String t = searchBookItem.getTitle();
                String is = searchBookItem.getIsbn();
                String img = searchBookItem.getImg();

                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("title", t);  //Intent는 데이터를 extras 키-값 쌍으로 전달
                intent.putExtra("isbn", is);
                intent.putExtra("img",imgurl);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
