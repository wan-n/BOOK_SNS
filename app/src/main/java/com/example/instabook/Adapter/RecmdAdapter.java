package com.example.instabook.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class RecmdAdapter extends BaseAdapter {
    private static final String TAG = "RecmdAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    SaveSharedPreference sp;
    int useruid;
    int himge;
    RecmdBookItem recmdBookItem;

    ArrayList<RecmdBookItem> items = new ArrayList<RecmdBookItem>();

    public RecmdAdapter(Context context, int layout, ArrayList<RecmdBookItem> items) {
        this.context = context;
        this.items = items;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    static class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView isbnTextView;
        TextView pubTextView;
        ImageButton jjimbtn;
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

    public void setItem(int userbookuid, int position) { items.get(position).setUserBookUID(userbookuid);}

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int uid = sp.getUserUid(context.getApplicationContext());
        useruid = uid;
        ViewHolder hodler;
        int pos = position;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_recmd, parent, false);
            hodler = new ViewHolder();
            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            hodler.iconImageView = (ImageView) convertView.findViewById(R.id.img_book);
            hodler.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            hodler.isbnTextView = (TextView) convertView.findViewById(R.id.text_isbn);
            hodler.pubTextView = (TextView) convertView.findViewById(R.id.text_pub);
            hodler.jjimbtn = (ImageButton) convertView.findViewById(R.id.imgbtn_favorite);

            convertView.setTag(hodler);
        } else{
            hodler = (ViewHolder)convertView.getTag();
        }

        //item 가져오기
        recmdBookItem = getItem(pos);
        himge = setheart(recmdBookItem);

        hodler.iconImageView.setImageBitmap(recmdBookItem.getRImgbm());
        hodler.titleTextView.setText(recmdBookItem.getRbname());
        hodler.isbnTextView.setText(recmdBookItem.getRisbn());
        hodler.pubTextView.setText(recmdBookItem.getRpub());
        hodler.jjimbtn.setImageResource(himge);

        //찜 버튼 클릭
        hodler.jjimbtn.setTag(pos);
        hodler.jjimbtn.setOnClickListener(jjimOnClickListener);

        return convertView;
    }

    final Button.OnClickListener jjimOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt((v.getTag().toString()));
            RecmdBookItem reitem = items.get(position);

            String isbn =reitem.getRisbn();
            int ubuid = reitem.getUserBookUID();

            HashMap<String, Object> map = new HashMap<>();
            map.put("uid",useruid);
            map.put("isbn",isbn);

            if (ubuid > 0){ //찜 목록에서 없애기
                Retrofit retro_djim = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_djim.create(RetroBaseApiService.class);

                retroBaseApiService.delUBook(ubuid).enqueue(new Callback<UserBookData>() {
                    @Override
                    public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                        Toast.makeText(context.getApplicationContext(), reitem.getRbname()+"찜 도서 제거 성공", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(Call<UserBookData> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "찜 도서 제거 실패", Toast.LENGTH_SHORT).show();
                    }

                });
            } else { //찜목록에 넣기
                Retrofit retro_jjim = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_jjim.create(RetroBaseApiService.class);

                retroBaseApiService.postUBook(map).enqueue(new Callback<UserBookData>() {
                    @Override
                    public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                        Toast.makeText(context.getApplicationContext(), reitem.getRbname()+"찜 도서 추가 성공", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(Call<UserBookData> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "찜 도서 추가 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    };

    private int setheart(RecmdBookItem item){
        int himg;
        int bid = item.getUserBookUID();
        if(bid == 0){
            himg = R.drawable.favorite_border_black;
        } else {
            himg = R.drawable.favorite_black;
        }
        return himg;
    }
}