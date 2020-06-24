package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instabook.Activity.ForBook.BookData;
import com.example.instabook.Activity.ForBook.SearchdbActivity;
import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.SearchBookItem;
import com.example.instabook.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
import retrofit2.http.Url;

import static com.example.instabook.Activity.ForReview.ModiReviewActivity.retroBaseApiService;

public class BookListAdapter extends BaseAdapter {
    private static final String TAG = "BookListAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    ArrayList<SearchBookItem> items = new ArrayList<SearchBookItem>();
    SearchBookItem searchBookItem;

    // ListViewAdapter의 생성자
    public BookListAdapter(Context context, int layout, ArrayList<SearchBookItem> books) {
        this.context = context;
        this.items = books;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return items.size();
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public SearchBookItem getItem(int position) {
        return items.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView bookImageView;
        TextView titleTextView ;
        TextView authorTextView;
        TextView pubTextView;
        Button choiceButton;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int pos = position;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_searchbook, parent, false);
            holder = new ViewHolder();
            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            holder.bookImageView = (ImageView) convertView.findViewById(R.id.img_book) ;
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title) ;
            holder.authorTextView = (TextView) convertView.findViewById(R.id.text_author) ;
            holder.pubTextView = (TextView) convertView.findViewById(R.id.text_pub) ;
            holder.choiceButton = (Button)convertView.findViewById(R.id.pick_btn);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //item 가져오기
        searchBookItem = getItem(pos);
        String url = searchBookItem.getImg();

        Glide.with(convertView).load(url).override(70,70).into(holder.bookImageView);
        //holder.bookImageView.setImageBitmap(searchBookItem.getImgbm());
        holder.titleTextView.setText(searchBookItem.getTitle());
        holder.authorTextView.setText(searchBookItem.getAuthor());
        holder.pubTextView.setText(searchBookItem.getPublisher());

        holder.choiceButton.setTag(pos);
        holder.choiceButton.setOnClickListener(ChoiceOnClickListener);

        return convertView;
    }

    final Button.OnClickListener ChoiceOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt((v.getTag().toString()));
            SearchBookItem sbitem = items.get(position);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            String t = sbitem.getTitle();
            String is = sbitem.getIsbn();
            Bitmap img  = sbitem.getImgbm();
            img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();

            Intent intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("title", t);  //Intent는 데이터를 extras 키-값 쌍으로 전달
            intent.putExtra("isbn", is);
            intent.putExtra("img", bytes);
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    };
}
