package com.example.instabook.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.instabook.Activity.ForBook.SearchdbActivity;
import com.example.instabook.R;

public class SearchFragment extends Fragment{

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View rootView;
    Button button;
    EditText searchBook;
    String keyword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //inflater를 이용하여 fragment_search.xml로 연결

        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        button = (Button) rootView.findViewById(R.id.searchBookBtn);

        button.setOnClickListener((View.OnClickListener) v -> {

            searchBook = (EditText) rootView.findViewById(R.id.searchBook);
            keyword = searchBook.getText().toString(); //editView의 텍스트를 String으로 keyword에 저장

            //intent로 데이터 전달
            Intent intent = new Intent(getActivity(), SearchdbActivity.class);
            intent.putExtra("keyword",keyword);  //Intent는 데이터를 extras 키-값 쌍으로 전달
            startActivity(intent);
        });

        return rootView; //view를 불러왔으니 view 리턴
    }


    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


    }
}
