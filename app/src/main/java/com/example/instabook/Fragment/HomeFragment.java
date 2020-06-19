package com.example.instabook.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.ForHome.UserData;
import com.example.instabook.Activity.MainActivity;
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
    Context context;
    List<UserData> frDataList;
    List<UserData> allUserlist;
    UserData uid;

    List<HomeData> homeDataList;
    ArrayList<HomeReviewItem> items;
    HomeReviewItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(getActivity());

        //친구 리스트 가져오기
        Retrofit retro_id = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_id.create(RetroBaseApiService.class);

        retroBaseApiService.getUid(useruid).enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                //친구 정보 있음
                frDataList = response.body();

                //유저 UID + 친구 UID 리스트 만들기
                allUserlist = new ArrayList<>();
                for(int j = 0; j < frDataList.size(); j++){
                    int fruid = frDataList.get(j).getFriendUID();
                    uid = new UserData(fruid);

                    allUserlist.add(uid);
                }
                uid = new UserData(useruid);
                allUserlist.add(uid);

                //각 유저마다의 정보 가져오기
                for(int i = 0; i < allUserlist.size(); i++){
                    //유저 UID로 유저 정보, 리뷰 정보, 도서 정보 가져오기
                    int userUID = allUserlist.get(i).getUserUID();

                    Retrofit retro_info = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_info.create(RetroBaseApiService.class);

                    retroBaseApiService.getHreq(userUID).enqueue(new Callback<List<HomeData>>() {
                        @Override
                        public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                            //리뷰 정보 가져오기
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


                                //ReviewUID로 Tag리스트 가져오기


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
                                        items.add(item);

                                        initView();
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
                Retrofit retro_info = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_info.create(RetroBaseApiService.class);
                //유저 UID로 유저 정보, 리뷰 정보, 도서 정보 가져오기
                retroBaseApiService.getHreq(useruid).enqueue(new Callback<List<HomeData>>() {
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
                                    items.add(item);

                                    initView();
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
        });

        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(){
        HomeReviewAdapter hrAdapter = new HomeReviewAdapter(getActivity(), R.layout.listview_homereview, items);
        ListView listview = (ListView) getView().findViewById(R.id.home_listview);
        listview.setAdapter(hrAdapter);

        listview.setAdapter(hrAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hrAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}