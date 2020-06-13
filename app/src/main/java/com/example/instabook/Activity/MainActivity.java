package com.example.instabook.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.instabook.Activity.Pre.LoginActivity;
import com.example.instabook.Adapter.ContentsPagerAdapter;
import com.example.instabook.R;
import com.google.android.material.tabs.TabLayout;

import static com.example.instabook.R.id.layout_tab;


public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private ContentsPagerAdapter mContentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userid = SaveSharedPreference.getUserName(MainActivity.this);

        //로그인이 안되어있는 상태일 시 로그인 화면으로 이동
        if(userid.length() <= 0){
            Intent home = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(home);
        }

        mContext=getApplicationContext();

        mTabLayout = (TabLayout)findViewById(layout_tab);

        mTabLayout.addTab(mTabLayout.newTab().setText("홈"));
        mTabLayout.addTab(mTabLayout.newTab().setText("추천"));
        mTabLayout.addTab(mTabLayout.newTab().setText("검색"));
        mTabLayout.addTab(mTabLayout.newTab().setText("내정보"));

        mViewPager = (ViewPager)findViewById(R.id.pager_content);
        mContentPagerAdapter = new ContentsPagerAdapter(getSupportFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(mContentPagerAdapter);

        //ViewPager 의 페이지가 변경될 때 TabLayout 에도 알려주는 리스너
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //Tab이 선택되었을 때 알려주는 리스너
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { //탭이 선택되었을 때, 호출됨
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { //탭이 선택되지 않았을 때, 호출됨

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { //탭이 다시 선택되었을 때, 호출됨

            }
        });
    }


    //설정버튼 활성화
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings: //설정 메뉴 화면으로 이동
                Intent set = new Intent(getApplicationContext(), SettingMenuActivity.class);
                startActivity(set);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private View createTabView(String tabName){ //TextView에 넣을 제목을 받아 처리뒤, View를 리턴, setCustomView() 통해 적용
        View tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab,null);
        TextView txt_name = (TextView)tabView.findViewById(R.id.txt_name);
        txt_name.setText(tabName);
        return tabView;
    }


    //뒤로가기 2번 클릭 시 종료
    private long lastTimeBackPressed; //뒤로가기 버튼이 클릭된 시간
    @Override
    public void onBackPressed()
    {

        //2초 이내에 뒤로가기 버튼을 재 클릭 시 앱 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 2000)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        //'뒤로' 버튼 한번 클릭 시 메시지
        Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        //lastTimeBackPressed에 '뒤로'버튼이 눌린 시간을 기록
        lastTimeBackPressed = System.currentTimeMillis();
    }
}

