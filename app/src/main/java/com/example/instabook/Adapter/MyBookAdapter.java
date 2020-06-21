package com.example.instabook.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.ForMyBook.MyBookActivity;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Fragment.InfoFragment;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.ListView.UserBookItem;
import com.example.instabook.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class MyBookAdapter extends BaseAdapter {
    private static final String TAG = "MyBookAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    SaveSharedPreference sp;
    ArrayList<UserBookItem> items = new ArrayList<UserBookItem>();
    UserBookItem userBookItem;

    public MyBookAdapter(Context context, int layout, ArrayList<UserBookItem> items) {
        this.context = context;
        this.items = items;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public UserBookItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView pubTextView;
        ImageButton clearButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int uid = sp.getUserUid(context.getApplicationContext());
        ViewHolder hodler;
        int pos = position;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_mybook, parent, false);
            hodler = new ViewHolder();
            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            hodler.iconImageView = (ImageView) convertView.findViewById(R.id.mb_icon);
            hodler.titleTextView = (TextView) convertView.findViewById(R.id.mb_title);
            hodler.authorTextView = (TextView) convertView.findViewById(R.id.mb_author);
            hodler.pubTextView = (TextView) convertView.findViewById(R.id.mb_pub);
            hodler.clearButton = (ImageButton) convertView.findViewById(R.id.mb_clear);

            convertView.setTag(hodler);
        } else{
            hodler = (ViewHolder)convertView.getTag();
        }

        //item 가져오기
        userBookItem = getItem(pos);

        hodler.iconImageView.setImageBitmap(userBookItem.getBitmap());
        hodler.titleTextView.setText(userBookItem.getBookName());
        hodler.authorTextView.setText(userBookItem.getAuthor());
        hodler.pubTextView.setText(userBookItem.getPublisher());
        hodler.clearButton.setImageResource(R.drawable.clear_black);

        //삭제 버튼 클릭
        hodler.clearButton.setTag(pos);
        hodler.clearButton.setOnClickListener(clearOnClickListener);

        return convertView;
    }

    final Button.OnClickListener clearOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt((v.getTag().toString()));
            UserBookItem ubitem = items.get(position);

            int ubuid = ubitem.getUserBookUID();

            Retrofit retro_djim = new Retrofit.Builder()
                    .baseUrl(retroBaseApiService.Base_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            retroBaseApiService = retro_djim.create(RetroBaseApiService.class);

            retroBaseApiService.delUBook(ubuid).enqueue(new Callback<UserBookData>() {
                @Override
                public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                    notifyDataSetChanged();
                    Toast.makeText(context.getApplicationContext(), ubitem.getBookName() + " 찜 목록에서 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<UserBookData> call, Throwable t) {
                    Toast.makeText(context.getApplicationContext(), "찜 도서 제거 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}


