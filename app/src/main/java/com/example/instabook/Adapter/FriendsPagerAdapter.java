package com.example.instabook.Adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.instabook.Fragment.Friends.FListFragment;
import com.example.instabook.Fragment.Friends.FRequestFragment;

public class FriendsPagerAdapter extends FragmentPagerAdapter {

    int mNumOfTabs;

    public FriendsPagerAdapter(FragmentManager fm, int numTabs){
        super(fm);
        this.mNumOfTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch(position){
            case 0:
                FListFragment tab1 = new FListFragment();
                return tab1;
            case 1:
                FRequestFragment tab2 = new FRequestFragment();
                return tab2;
            default:
                return null;
        }
        //return null;
    }

    @Override
    public int getCount(){
        return mNumOfTabs;
    }
}
