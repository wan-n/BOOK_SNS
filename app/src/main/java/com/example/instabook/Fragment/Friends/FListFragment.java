package com.example.instabook.Fragment.Friends;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Activity.SearchFriendActivity;
import com.example.instabook.Adapter.FriendListAdapter;
import com.example.instabook.Adapter.RequestFriendAdapter;
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


public class FListFragment extends Fragment {

    ArrayList<SearchFriendItem> items;
    RetroBaseApiService retroBaseApiService;
    SearchFriendItem mi;
    SaveSharedPreference sp;


    public FListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //유저 아이디가져오기
        final String userid = sp.getUserName(getActivity());


        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.getFrd(userid).enqueue(new Callback<List<ResponseGet>>() {
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
                            Toast.makeText(getActivity(), "실패", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                FriendListAdapter flAdapter = new FriendListAdapter(getActivity(), R.layout.listview_friendlist, items);

                ListView myList;
                myList = (ListView) getView().findViewById(R.id.fl_list);
                myList.setAdapter(flAdapter);

            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                //Toast.makeText(getActivity(), "페이지를 다시 로드해주세요.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flist, container, false);
    }
}
