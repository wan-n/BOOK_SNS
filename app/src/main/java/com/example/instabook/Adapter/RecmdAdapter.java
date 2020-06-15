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

import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.R;

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
    ImageButton btn = null;
    int useruid2;
    UserBookUIDData userbookUID;

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

    public void setItem(int userbookuid, int position) { items.get(position).setUserBookUID(userbookuid);}

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position;
        final int useruid = sp.getUserUid(context.getApplicationContext());
        useruid2 = useruid;
        Log.d(TAG,"현 사용자 uid  "+useruid+"  옮겨온 사용자 uid : "+useruid2);


        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_recmd, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.img_book);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.text_title);
        TextView isbnTextView = (TextView) convertView.findViewById(R.id.text_isbn);
        TextView pubTextView = (TextView) convertView.findViewById(R.id.text_pub);
        btn = (ImageButton) convertView.findViewById(R.id.imgbtn_favorite);

        RecmdBookItem recmdBookItem = getItem(pos);

        iconImageView.setImageResource(R.drawable.default_img);
        titleTextView.setText(recmdBookItem.getRbname());
        isbnTextView.setText(recmdBookItem.getRisbn());
        pubTextView.setText(recmdBookItem.getRpub());

        final int[] jjim = {0};
        String isbn = recmdBookItem.getRisbn();

        btn.setImageResource(R.drawable.favorite_border_black);
        Retrofit retro_id = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_id.create(RetroBaseApiService.class);

        retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
            @Override
            public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                userbookUID = response.body();

                jjim[0] =1;
                int userbookuid = userbookUID.getUserBookUID();
                recmdBookItem.setUserBookUID(userbookuid);

                btn.setImageResource(R.drawable.favorite_black);
                Log.d(TAG,"찜 도서 정보 있음");
            }
            @Override
            public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                Log.d(TAG, "찜 도서 정보 없음 ");
            }
        });


        //찜 버튼 클릭
        btn.setTag(pos);
        btn.setOnClickListener(jjimOnClickListener);
        return convertView;
    }

    final Button.OnClickListener jjimOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"온 클릭 이벤트 들어옴");
            int position = Integer.parseInt((v.getTag().toString()));
            RecmdBookItem reitem = items.get(position);

            String isbn =reitem.getRisbn();
            int ubuid = reitem.getUserBookUID();
            Log.d(TAG,"선택된 유저 북아이디: "+ubuid);

            HashMap<String, Object> map = new HashMap<>();
            map.put("uid",useruid2);
            map.put("isbn",isbn);

            if (userbookUID == null){  //찜목록에 넣기
                Retrofit retro_jjim = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_jjim.create(RetroBaseApiService.class);

                retroBaseApiService.postUBook(map).enqueue(new Callback<UserBookData>() {
                    @Override
                    public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                        btn.setImageResource(R.drawable.favorite_black);
                        Toast.makeText(context.getApplicationContext(), "찜 도서 추가 성공", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<UserBookData> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "찜 도서 추가 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            } else { //찜 목록에서 없애기
                Retrofit retro_djim = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_djim.create(RetroBaseApiService.class);

                retroBaseApiService.delUBook(ubuid).enqueue(new Callback<UserBookData>() {
                    @Override
                    public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                        btn.setImageResource(R.drawable.favorite_border_black);
                        Toast.makeText(context.getApplicationContext(), "찜 도서 제거 성공", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<UserBookData> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "찜 도서 제거 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}