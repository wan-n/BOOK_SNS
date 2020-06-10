package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.Pre.ChangePwdActivity;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Adapter.FriendsPagerAdapter;
import com.example.instabook.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity {

    RetroBaseApiService retroBaseApiService;

    ImageView fr_back, fr_add;
    TextView fr_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        fr_back = findViewById(R.id.fr_back);
        fr_add = findViewById(R.id.fr_add);
        fr_search = findViewById(R.id.fr_search);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //뒤로가기
                    case R.id.fr_back:
                        onBackPressed();
                        break;
                    case R.id.fr_add:
                        String search = fr_search.getText().toString();
                        final String get_name = search.trim();
                        Intent get_tv = new Intent(getApplicationContext(), SearchFriendActivity.class);
                        get_tv.putExtra("search", get_name);
                        startActivity(get_tv);
                        break;
                }

            }
        };

        fr_back.setOnClickListener(listener);
        fr_add.setOnClickListener(listener);



        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("친구 목록"));
        tabs.addTab(tabs.newTab().setText("받은 요청"));
        tabs.setTabGravity(tabs.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final FriendsPagerAdapter friendsPagerAdapter = new FriendsPagerAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(friendsPagerAdapter);

        //탭 선택 이벤트
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
