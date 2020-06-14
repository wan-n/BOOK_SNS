package com.example.instabook.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;


public class RecmdFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    SaveSharedPreference sp;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recmd, container, false);



        // Inflate the layout for this fragment
        return rootView;
    }
}
