package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.SearchBookItem;
import com.example.instabook.R;

import java.util.ArrayList;

public class BookListAdapter extends BaseAdapter {
    RetroBaseApiService retroService;
    SaveSharedPreference sp;
    private static final String TAG = "BookListAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    ArrayList<SearchBookItem> books;

    // ListViewAdapter의 생성자
    public BookListAdapter(Context context, int layout, ArrayList<SearchBookItem> books) {
        this.context = context;
        this.books = books;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    public BookListAdapter() {

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
        final int pos = position;
        //final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_searchbook, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.img_book) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.text_title) ;
        TextView isbnTextView = (TextView) convertView.findViewById(R.id.text_isbn) ;
        TextView pubTextView = (TextView) convertView.findViewById(R.id.text_pub) ;
        Button btn = (Button)convertView.findViewById(R.id.pick_btn);

        SearchBookItem searchBookItem = getItem(pos);

        iconImageView.setImageResource(books.get(pos).iconDrawable);
        titleTextView.setText(searchBookItem.getTitle());
        isbnTextView.setText(searchBookItem.getIsbn());
        pubTextView.setText(searchBookItem.getPublisher());

        btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String t = books.get(pos).getTitle();
                String is = books.get(pos).getIsbn();

                Intent intent = new Intent(v.getContext(), ReviewActivity.class);
                intent.putExtra("title",t);  //Intent는 데이터를 extras 키-값 쌍으로 전달
                intent.putExtra("isbn", is);
                Log.d(TAG,"선택된 제목: " + t);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void addItem(String title, String author, String publisher) {
        ArrayList<SearchBookItem> books = new ArrayList<>();
        SearchBookItem mItem = new SearchBookItem(title, author, publisher);

        books.add(mItem);
    }
}
