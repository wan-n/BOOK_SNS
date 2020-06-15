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

import androidx.fragment.app.FragmentTransaction;

import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Fragment.Friends.FRequestFragment;
import com.example.instabook.Fragment.InfoFragment;
import com.example.instabook.ListView.SearchFriendItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestFriendAdapter extends BaseAdapter {


    Context maincon;
    LayoutInflater inflater;
    ArrayList<SearchFriendItem> items;
    RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;
    int layout;


    public RequestFriendAdapter(Context context, int layout, ArrayList<SearchFriendItem> items){
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

        //유저 아이디 가져오기
        final String userid = sp.getUserName(maincon);

        ImageView iconImageView = convertView.findViewById(R.id.rf_icon);
        iconImageView.setImageBitmap(items.get(position).iconDrawable);


        TextView nameTextView = convertView.findViewById(R.id.rf_name);
        nameTextView.setText(items.get(position).nameStr);

        Button btn_a = convertView.findViewById(R.id.rf_accept);
        Button btn_r = convertView.findViewById(R.id.rf_refuse);

        btn_a.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구의 닉네임 저장
                String username = items.get(pos).nameStr;

                HashMap<String, Object> userinfo = new HashMap<>();
                userinfo.put("id", userid);
                userinfo.put("name", username);

                Retrofit retro_name = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_name.create(RetroBaseApiService.class);

                retroBaseApiService.postFrd(userinfo).enqueue(new Callback<ResponseGet>() {
                    @Override
                    public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {

                        Toast.makeText(maincon, "친구 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();

                        //처리가 완료된 뷰는 삭제
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

        btn_r.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구의 닉네임 저장
                String username = items.get(pos).nameStr;

                Retrofit retro_name = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_name.create(RetroBaseApiService.class);

                retroBaseApiService.delReq(userid, username).enqueue(new Callback<ResponseGet>() {
                    @Override
                    public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {

                        Toast.makeText(maincon, "거절되었습니다.", Toast.LENGTH_SHORT).show();

                        //처리가 완료된 뷰는 삭제
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
