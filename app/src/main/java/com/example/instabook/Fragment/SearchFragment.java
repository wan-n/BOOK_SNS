package com.example.instabook.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.instabook.Activity.ForBook.SearchdbActivity;
import com.example.instabook.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
