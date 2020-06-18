package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Adapter.SearchFriendAdapter;
import com.example.instabook.ListView.SearchFriendItem;
import com.example.instabook.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFriendActivity extends AppCompatActivity {

    ArrayList<SearchFriendItem> items;
    RetroBaseApiService retroBaseApiService;
    SearchFriendItem mi;
    SaveSharedPreference sp;

    SearchFriendAdapter sfAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchfriend);

        final EditText sf_search;
        ImageView sf_add, sf_back;
        FrameLayout sf_fr_back;


        //이전화면에서 입력된 검색값 받아오기
        Intent get_tv = getIntent();
        String search = get_tv.getExtras().getString("search");

        sf_search = findViewById(R.id.sf_search);
        sf_add = findViewById(R.id.sf_add);
        sf_fr_back = findViewById(R.id.sf_fr_back);

        sf_search.setText(search);
        retroGet(search);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //뒤로가기
                    case R.id.sf_fr_back:
                        onBackPressed();
                        break;
                    case R.id.sf_add:

                        String re_search = sf_search.getText().toString().trim();
                        retroGet(re_search);


                        break;
                }
            }
        };

        sf_fr_back.setOnClickListener(listener);
        sf_add.setOnClickListener(listener);

    }

    void retroGet(String search){
        final String search_str = search.trim();
        if(search_str.length() <= 0){
            items.clear();
            sfAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            //유저 아이디, UID 가져오기
            final String userid = sp.getUserName(SearchFriendActivity.this);
            final int useruid = sp.getUserUid(SearchFriendActivity.this);

            Retrofit retro_name = new Retrofit.Builder()
                    .baseUrl(retroBaseApiService.Base_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            retroBaseApiService = retro_name.create(RetroBaseApiService.class);

            retroBaseApiService.getNickname(search_str, userid).enqueue(new Callback<List<ResponseGet>>() {
                @Override
                public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                    List<ResponseGet> nickname = response.body();
                    items = new ArrayList<>();
                    //아이템 추가
                    for(int i = 0; i < nickname.size(); i++){
                        String lv_name = nickname.get(i).getNickName();
                        int lv_uid = nickname.get(i).getUserUID();

                        //uid로 이미지 가져오기
                        Retrofit retro_imgFirst = new Retrofit.Builder()
                                .baseUrl(retroBaseApiService.Base_URL)
                                .addConverterFactory(GsonConverterFactory.create()).build();
                        retroBaseApiService = retro_imgFirst.create(RetroBaseApiService.class);

                        retroBaseApiService.getImage(lv_uid).enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                //서버에서 받아온 이미지 비트맵으로 변환
                                InputStream is = response.body().byteStream();
                                Bitmap bitmap_profile = BitmapFactory.decodeStream(is);

                                //리스트뷰에 추가
                                mi = new SearchFriendItem(bitmap_profile, lv_name);
                                items.add(mi);
                                //Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(SearchFriendActivity.this, "실패", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    sfAdapter = new SearchFriendAdapter(SearchFriendActivity.this, R.layout.listview_searchfriend, items);

                    ListView myList;
                    myList = findViewById(R.id.sf_list);
                    myList.setAdapter(sfAdapter);

                    //결과 화면 불러오기
                    sfAdapter.notifyDataSetChanged();

                    
                }

                @Override
                public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                    if(items != null){
                        items.clear();
                        sfAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
