package com.example.instabook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.R;

import java.util.ArrayList;

public class MyBookAdapter extends BaseAdapter {
    SaveSharedPreference sp;
    private static final String TAG = "MyBookAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    ArrayList<RecmdBookItem> items = new ArrayList<RecmdBookItem>();

    public MyBookAdapter(Context context, int layout, ArrayList<RecmdBookItem> items) {
        this.context = context;
        this.items = items;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    static class ViewHolder {
        TextView tx1;
        TextView tx2;
        ImageView img1;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int uid = sp.getUserUid(context.getApplicationContext());
        RecmdAdapter.ViewHolder hodler;
        int pos = position;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_recmd, parent, false);
            hodler = new RecmdAdapter.ViewHolder();
            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            hodler.iconImageView = (ImageView) convertView.findViewById(R.id.img_book);
            hodler.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            hodler.isbnTextView = (TextView) convertView.findViewById(R.id.text_isbn);
            hodler.pubTextView = (TextView) convertView.findViewById(R.id.text_pub);
            hodler.jjimbtn = (ImageButton) convertView.findViewById(R.id.imgbtn_favorite);

            convertView.setTag(hodler);
        } else{
            hodler = (RecmdAdapter.ViewHolder)convertView.getTag();
        }

        return null;
    }
}


