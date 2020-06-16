package com.example.instabook.Adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.instabook.Fragment.HomeFragment;
import com.example.instabook.Fragment.InfoFragment;
import com.example.instabook.Fragment.RecmdFragment;
import com.example.instabook.Fragment.SearchFragment;

//생성자를 통해서 Fragment의 관리를 도와주는 FragmentManager와 페이지의 개수를 탭의 개수와 맞춰주기 위해 pageCount를 받아옴
@SuppressWarnings("ALL")
public class ContentsPagerAdapter extends FragmentStatePagerAdapter {

    private int mPageCount;

    public ContentsPagerAdapter(FragmentManager fm, int pageCount){
        super(fm);
        this.mPageCount=pageCount;
    }

    @Override
    public Fragment getItem(int position) { //position에 해당하는 Fragment를 반환
        switch (position){
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                RecmdFragment recmdFragment = new RecmdFragment();
                return recmdFragment;
            case 2:
                SearchFragment searchFragment = new SearchFragment();
                return searchFragment;
            case 3:
                InfoFragment infoFragment = new InfoFragment();
                return infoFragment;


            default:
                return null;
        }
    }

    @Override
    public int getCount() { //page의 개수를 반환, 반환되는 수에 따라 페이지의 수가 결정됨, 탭의 개수도 동일
        return mPageCount;
    }
}
