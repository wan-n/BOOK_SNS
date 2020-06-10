package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instabook.Adapter.FriendsPagerAdapter;
import com.example.instabook.R;
import com.google.android.material.tabs.TabLayout;

public class FriendsActivity extends AppCompatActivity {

    ImageView fr_back, fr_add;
    EditText fr_search;
    FrameLayout fr_fr_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        fr_back = findViewById(R.id.fr_back);
        fr_add = findViewById(R.id.fr_add);
        fr_search = findViewById(R.id.fr_search);
        fr_fr_back = findViewById(R.id.fr_fr_back);

        fr_search.requestFocus();


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //뒤로가기
                    case R.id.fr_fr_back:
                        onBackPressed();
                        break;
                    case R.id.fr_add:
                        String search = fr_search.getText().toString();
                        final String get_name = search.trim();

                        if(get_name.length() <= 0){
                            Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }else {

                            Intent get_tv = new Intent(getApplicationContext(), SearchFriendActivity.class);
                            get_tv.putExtra("search", get_name);
                            startActivity(get_tv);
                        }
                        break;
                }

            }
        };

        fr_fr_back.setOnClickListener(listener);
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
