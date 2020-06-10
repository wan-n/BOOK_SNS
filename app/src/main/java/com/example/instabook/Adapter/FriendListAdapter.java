package com.example.instabook.Adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Activity.SearchFriendActivity;
import com.example.instabook.ListView.SearchFriendItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendListAdapter extends BaseAdapter {

    RetroBaseApiService retroService;
    SaveSharedPreference sp;

    Context maincon;
    LayoutInflater inflater;
    ArrayList<SearchFriendItem> items;
    int layout;

    public FriendListAdapter(Context context, int layout, ArrayList<SearchFriendItem> items){
        maincon = context;
        this.items = items;
        this.layout = layout;

        //리스트뷰에서 사용한 뷰를 정의한 xml을 읽어오기 위해 LayoutInflater 객체 생성
        inflater = LayoutInflater.from(maincon);
    }

    public int getCount(){
        return items.size();
    }

    public Object getItem(int position){
        return items.get(position).nameStr;
    }

    public long getItemId(int position){
        return position;
    }


    //각 항목의 뷰 생성
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        ImageView iconImageView = convertView.findViewById(R.id.fl_icon);
        iconImageView.setImageBitmap(items.get(position).iconDrawable);


        TextView nameTextView = convertView.findViewById(R.id.fl_name);
        nameTextView.setText(items.get(position).nameStr);

        Button btn = convertView.findViewById(R.id.fl_del);

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = items.get(pos).nameStr;

                //유저 아이디 가져오기
                final String userid = sp.getUserName(maincon);

                //친구의 닉네임 저장
                String username = items.get(pos).nameStr;

                Retrofit retro_name = new Retrofit.Builder()
                        .baseUrl(retroService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroService = retro_name.create(RetroBaseApiService.class);

                retroService.delFrd(userid, username).enqueue(new Callback<ResponseGet>() {
                    @Override
                    public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                        Toast.makeText(maincon, "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                        //삭제가 완료된 뷰는 삭제
                        items.remove(pos);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<ResponseGet> call, Throwable t) {
                        Toast.makeText(maincon, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        return convertView;
    }
}
