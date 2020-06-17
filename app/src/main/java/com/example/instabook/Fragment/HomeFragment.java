package com.example.instabook.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.instabook.Activity.ForHome.AllUserData;
import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.ForHome.UserData;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.HomeReviewAdapter;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.R;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";
    SaveSharedPreference sp;
    View rootView;

    List<UserData> frDataList;
    List<AllUserData> allUserDataList;
    AllUserData alluserData;
    AllUserData alluserData2;

    List<HomeData> homeDataList;
    ArrayList<HomeReviewItem> items;
    HomeReviewItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //유저 UID 가져오기
        final int useruid1 = sp.getUserUid(getActivity());

        Retrofit retro_id = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_id.create(RetroBaseApiService.class);
        //유저 UID와 친구 UID 리스트 만들기
        retroBaseApiService.getUid(useruid1).enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                frDataList = response.body();

                int w = 0;
                for(int j = 0; j < frDataList.size(); j++){
                    int fruid1 = frDataList.get(j).getFriendUID();

                    alluserData = new AllUserData(fruid1);
                    int uruid1 = alluserData.getUserUID();

                    allUserDataList = new ArrayList<>();
                    allUserDataList.add(alluserData);

                    if(w == 0){
                        alluserData2 = new AllUserData(useruid1);
                        allUserDataList.add(alluserData2);
                    }
                }

                for(int i = 0; i < allUserDataList.size(); i++){
                    //각 유저마다의 정보 가져오기
                    Retrofit retro_info = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_info.create(RetroBaseApiService.class);
                    //유저 UID로 유저 정보, 리뷰 정보, 도서 정보 가져오기
                    int userUID = allUserDataList.get(i).getUserUID();
                    retroBaseApiService.getHreq(userUID).enqueue(new Callback<List<HomeData>>() {
                        @Override
                        public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                            homeDataList = response.body();
                            items = new ArrayList<>();

                            for(int l = 0; l < homeDataList.size(); l++){

                                int uid = homeDataList.get(l).getUserUID();
                                String review = homeDataList.get(l).getReview();
                                String redate = homeDataList.get(l).getReviewDate();
                                String isbn = homeDataList.get(l).getISBN13();
                                int rate = homeDataList.get(l).getRate();
                                String bname = homeDataList.get(l).getBookName();
                                String nname = homeDataList.get(l).getNickName();

                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(redate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                    String redate_2 = sdf.format(date);

                                //uid로 이미지 가져오기
                                Retrofit retro_imgFirst = new Retrofit.Builder()
                                        .baseUrl(retroBaseApiService.Base_URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                retroBaseApiService = retro_imgFirst.create(RetroBaseApiService.class);

                                retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        //서버에서 받아온 이미지 비트맵으로 변환
                                        InputStream is = response.body().byteStream();
                                        Bitmap bitmap_profile = BitmapFactory.decodeStream(is);

                                        //리스트뷰에 추가
                                        item = new HomeReviewItem(bitmap_profile, uid, review, redate_2, isbn, rate, bname, nname);
                                        Log.d(TAG,"유아이디: "+uid+"리뷰: "+review+"날짜: "+redate+"ISBN: "+isbn+"별점: "+rate+"제목: "+bname+"닉네임: "+nname);
                                        items.add(item);

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                HomeReviewAdapter hrAdapter = new HomeReviewAdapter(getActivity(), R.layout.listview_homereview, items);
                                                ListView listView = (ListView) getView().findViewById(R.id.home_listview);

                                                listView.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listView.setAdapter(hrAdapter);
                                                    }
                                                });
                                            }
                                        }).start();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<List<HomeData>> call, Throwable t) {
                            Toast.makeText(getActivity(), "리뷰 정보 없음.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {

            }
        });

        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

}