package com.example.instabook.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.instabook.Adapter.FriendsPagerAdapter;
import com.example.instabook.R;
import com.google.android.material.tabs.TabLayout;

public class FriendsActivity extends AppCompatActivity {

    private ImageView fr_back, fr_add;
    private EditText fr_search;
    private FrameLayout fr_fr_back;
    private FriendsPagerAdapter fpAdapter;
    private ViewPager viewPager;
    private TabLayout tabs;
    private Context mContext;



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



        mContext=getApplicationContext();

        tabs = findViewById(R.id.tabs);


        tabs.addTab(tabs.newTab().setText("친구 목록"));
        tabs.addTab(tabs.newTab().setText("받은 요청"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fpAdapter = new FriendsPagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        viewPager.setAdapter(fpAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        //탭 선택 이벤트
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) { //탭이 선택되었을 때, 호출됨
                Intent intent = getIntent();

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Intent intent = getIntent();
            }
        });


    }


    private View createTabView(String tabName){ //TextView에 넣을 제목을 받아 처리뒤, View를 리턴, setCustomView() 통해 적용
        View tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab,null);
        TextView txt_name = (TextView)tabView.findViewById(R.id.txt_name);
        txt_name.setText(tabName);
        return tabView;
    }



    public void refresh(){
        fpAdapter.notifyDataSetChanged();
    }


    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fpAdapter.notifyDataSetChanged();
    }
}
