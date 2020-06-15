package com.example.instabook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.ListView.SearchBookItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class RecmdAdapter extends BaseAdapter {
    int layout;
    Context context;
    LayoutInflater inflater;
    SaveSharedPreference sp;
    ArrayList<RecmdBookItem> items = new ArrayList<>();

    public RecmdAdapter(Context context, int layout, ArrayList<RecmdBookItem> items) {
        this.context = context;
        this.items = items;
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
    public RecmdBookItem getItem(int position) {
        return items.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position;

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
        ImageButton btn = (ImageButton) convertView.findViewById(R.id.imgbtn_favorite);

        RecmdBookItem recmdBookItem = getItem(pos);

        iconImageView.setImageResource(R.drawable.default_img);
        titleTextView.setText(recmdBookItem.getRbname());
        isbnTextView.setText(recmdBookItem.getRisbn());
        pubTextView.setText(recmdBookItem.getRpub());

        final int useruid = sp.getUserUid(context.getApplicationContext());
        String isbn = recmdBookItem.getRisbn();

        Retrofit retro_id = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_id.create(RetroBaseApiService.class);
        //유저 UID와 친구 UID 리스트 만들기
        retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
            @Override
            public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                if(response.body() == null){
                    btn.setImageResource(R.drawable.favorite_border_black);
                } else {
                    btn.setImageResource(R.drawable.favorite_black);
                }
            }

            @Override
            public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                btn.setImageResource(R.drawable.favorite_border_black);
            }
        });

        return null;
    }


}
