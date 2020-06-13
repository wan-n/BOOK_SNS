package com.example.instabook.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.instabook.Activity.ForBook.SearchdbActivity;
import com.example.instabook.Activity.ForHome.AllUserData;
import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.ForHome.UserData;
import com.example.instabook.Activity.ForReview.ReviewActivity;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.HomeReviewAdapter;
import com.example.instabook.ListView.HomeReviewItem;
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

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    SaveSharedPreference sp;
    View rootView;

    List<UserData> frDataList;
    List<AllUserData> allUserDataList;
    AllUserData alluserData;
    AllUserData alluserData2;

    List<HomeData> homeDataList;
    UserBookData uBookData;
    UserBookData userBookData;
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

        Log.d(TAG,"사용자 유아이디 1: " + useruid1);

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

                                Retrofit retro_ubid = new Retrofit.Builder()
                                        .baseUrl(retroBaseApiService.Base_URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                retroBaseApiService = retro_ubid.create(RetroBaseApiService.class);
                                //유저uid와 isbn으로 소유도서정보 가져오기
                                retroBaseApiService.getUBid(uid,isbn).enqueue(new Callback<UserBookData>() {
                                    @Override
                                    public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                                        uBookData = response.body();

                                        Integer ubuid;
                                        if(uBookData == null){
                                            ubuid = -1;
                                        } else {
                                            ubuid = uBookData.getUserBookUID();
                                        }

                                        userBookData = new UserBookData(ubuid);
                                    }

                                    @Override
                                    public void onFailure(Call<UserBookData> call, Throwable t) {
                                        Toast.makeText(getActivity(), "소유 도서 정보 없음", Toast.LENGTH_SHORT).show();
                                        Integer ubuid = -1;
                                        userBookData = new UserBookData(ubuid);
                                    }

                                });

                                Integer ubid ;
                                if(userBookData == null){
                                    ubid = -1;
                                } else {
                                    ubid = userBookData.getUserBookUID();
                                }
                                Log.d(TAG,"소유 도서 유아이디: "+ ubid);

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
                                        item = new HomeReviewItem(bitmap_profile, uid, review, redate, isbn, rate, bname, nname, ubid);
                                        items.add(item);
                                        //Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getActivity(), "이미지 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            HomeReviewAdapter hrAdapter = new HomeReviewAdapter(getActivity(), R.layout.listview_homereview, items);
                            ListView listView = (ListView) getView().findViewById(R.id.home_listview);
                            listView.setAdapter(hrAdapter);
                        }

                        @Override
                        public void onFailure(Call<List<HomeData>> call, Throwable t) {
                            Toast.makeText(getActivity(), "리뷰 정보 실패.", Toast.LENGTH_SHORT).show();
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
}