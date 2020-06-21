package com.example.instabook.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.ForHome.UserData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.HomeReviewAdapter;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.ListView.SearchBookItem;
import com.example.instabook.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";
    SaveSharedPreference sp;
    View rootView;
    Context context;
    private List<UserData> frDataList;
    private List<UserData> allUserlist;
    private UserData uid;
    private UserBookUIDData userbookUID;

    private List<HomeData> homeDataList;
    private List<HomeData> taglist;
    private ArrayList<HomeReviewItem> items;
    private HomeReviewItem item;

    Bitmap bm;

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
                Log.d(TAG,"친구 있다!");

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

                    int userUID = allUserlist.get(i).getUserUID();

                    //유저 UID로  리뷰 정보 가져오기
                    Retrofit retro_info = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_info.create(RetroBaseApiService.class);

                    retroBaseApiService.getHreq(userUID).enqueue(new Callback<List<HomeData>>() {
                        @SuppressLint("SimpleDateFormat")
                        @Override
                        public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                            Log.d(TAG,"친구 있고 리뷰 있다!");

                            homeDataList = response.body();
                            items = new ArrayList<>();

                            //리뷰에 정보 더하기
                            for(int l = 0; l < homeDataList.size(); l++){
                                int uid = homeDataList.get(l).getUserUID();
                                int ruid = homeDataList.get(l).getReviewUID();
                                String review = homeDataList.get(l).getReview();
                                String redate = homeDataList.get(l).getReviewDate();
                                String isbn = homeDataList.get(l).getISBN13();
                                String url = homeDataList.get(l).getBookImageUrl();
                                int rate = homeDataList.get(l).getRate();
                                String bname = homeDataList.get(l).getBookName();
                                String nname = homeDataList.get(l).getNickName();

                                //날짜변환
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(redate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                    String redate_2 = sdf.format(date);

                                //ReviewUID로 Tag 리스트 가져오기
                                Retrofit retro_info = new Retrofit.Builder()
                                        .baseUrl(retroBaseApiService.Base_URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                retroBaseApiService = retro_info.create(RetroBaseApiService.class);

                                retroBaseApiService.getReviewtag(ruid).enqueue(new Callback<List<HomeData>>() {
                                    @Override
                                    public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 있다!");

                                        taglist = response.body();

                                        String tags = new String();
                                        for(int w =0; w<taglist.size(); w++){
                                            tags += "#" + taglist.get(w).getTag() +" ";
                                        }
                                        String secondTags = tags;

                                        //리뷰 도서 중 찜 도서에 저장된 정보 가져오기
                                        Retrofit retro_id = new Retrofit.Builder()
                                                .baseUrl(retroBaseApiService.Base_URL)
                                                .addConverterFactory(GsonConverterFactory.create()).build();
                                        retroBaseApiService = retro_id.create(RetroBaseApiService.class);

                                        retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
                                            @Override
                                            public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                                                Log.d(TAG,"친구 있고 리뷰 있고 태그 있고 찜 도서 있다!");

                                                userbookUID = response.body();
                                                int ubid = userbookUID.getUserBookUID();

                                                String finalTags = secondTags;

                                                //uid로 이미지 가져오기
                                                Retrofit retro_imgFirst = new Retrofit.Builder()
                                                        .baseUrl(retroBaseApiService.Base_URL)
                                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                                retroBaseApiService = retro_imgFirst.create(RetroBaseApiService.class);

                                                retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 있고 찜 도서 있고 이미지 있다! ");

                                                        //서버에서 받아온 이미지 비트맵으로 변환
                                                        InputStream is = response.body().byteStream();
                                                        Bitmap bitmap_profile = BitmapFactory.decodeStream(is);


                                                        if(url == null){
                                                            //기본 이미지 비트맵으로 변환
                                                            Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                            int height = bmm.getHeight();
                                                            int width = bmm.getWidth();

                                                            Bitmap resized = null;
                                                            while(height>70){
                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                height = resized.getHeight();
                                                                width = resized.getWidth();
                                                            }
                                                            bm = resized;

                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, finalTags, ubid);
                                                            items.add(item);
                                                            initView();
                                                        } else {
                                                            Thread bthread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        URL urll = new URL(url);
                                                                        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                        conn.connect();
                                                                        InputStream bis = conn.getInputStream();
                                                                        Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                        int height = bmm.getHeight();
                                                                        int width = bmm.getWidth();

                                                                        Bitmap resized = null;
                                                                        if(height>width){
                                                                            while(height>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        } else {
                                                                            while(width>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        }

                                                                        bm = resized;
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }); bthread.start();
                                                            try {
                                                                bthread.join();
                                                                //리스트뷰에 추가
                                                                item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                        review, redate_2, isbn, rate, bname, nname, finalTags, ubid);
                                                                items.add(item);
                                                                initView();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 있고 찜 도서 있고 이미지 없다!");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                                                Log.d(TAG,"친구 있고 리뷰 있고 태그 있고 찜 도서 없다!");

                                                String finalTags = secondTags;

                                                //uid로 이미지 가져오기
                                                Retrofit retro_imgFirst = new Retrofit.Builder()
                                                        .baseUrl(retroBaseApiService.Base_URL)
                                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                                retroBaseApiService = retro_imgFirst.create(RetroBaseApiService.class);

                                                retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 있고 찜 도서 없고 이미지 있다! ");

                                                        //서버에서 받아온 이미지 비트맵으로 변환
                                                        InputStream is = response.body().byteStream();
                                                        Bitmap bitmap_profile = BitmapFactory.decodeStream(is);


                                                        if(url == null){
                                                            //기본 이미지 비트맵으로 변환
                                                            Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                            int height = bmm.getHeight();
                                                            int width = bmm.getWidth();

                                                            Bitmap resized = null;
                                                            while(height>70){
                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                height = resized.getHeight();
                                                                width = resized.getWidth();
                                                            }
                                                            bm = resized;

                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, finalTags, 0);
                                                            items.add(item);
                                                            initView();
                                                        } else {
                                                            Thread bthread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        URL urll = new URL(url);
                                                                        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                        conn.connect();
                                                                        InputStream bis = conn.getInputStream();
                                                                        Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                        int height = bmm.getHeight();
                                                                        int width = bmm.getWidth();

                                                                        Bitmap resized = null;
                                                                        if(height>width){
                                                                            while(height>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        } else {
                                                                            while(width>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        }

                                                                        bm = resized;
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }); bthread.start();
                                                            try {
                                                                bthread.join();
                                                                //리스트뷰에 추가
                                                                item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                        review, redate_2, isbn, rate, bname, nname, finalTags, 0);
                                                                items.add(item);
                                                                initView();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 있고 찜 도서 없고 이미지 없다!");
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Call<List<HomeData>> call, Throwable t) {
                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 없다!");

                                        //리뷰 도서 중 찜 도서에 저장된 정보 가져오기
                                        Retrofit retro_id = new Retrofit.Builder()
                                                .baseUrl(retroBaseApiService.Base_URL)
                                                .addConverterFactory(GsonConverterFactory.create()).build();
                                        retroBaseApiService = retro_id.create(RetroBaseApiService.class);

                                        retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
                                            @Override
                                            public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                                                Log.d(TAG,"친구 있고 리뷰 있고 태그 없고 찜 도서 있다!");

                                                userbookUID = response.body();
                                                int ubid = userbookUID.getUserBookUID();

                                                //uid로 이미지 가져오기
                                                Retrofit retro_imgFirst = new Retrofit.Builder()
                                                        .baseUrl(retroBaseApiService.Base_URL)
                                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                                retroBaseApiService = retro_imgFirst.create(RetroBaseApiService.class);

                                                retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 없고 찜 도서 있고 이미지 있다!");

                                                        //서버에서 받아온 이미지 비트맵으로 변환
                                                        InputStream is = response.body().byteStream();
                                                        Bitmap bitmap_profile = BitmapFactory.decodeStream(is);
                                                        String tags = "";


                                                        if(url == null){
                                                            //기본 이미지 비트맵으로 변환
                                                            Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                            int height = bmm.getHeight();
                                                            int width = bmm.getWidth();

                                                            Bitmap resized = null;
                                                            while(height>70){
                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                height = resized.getHeight();
                                                                width = resized.getWidth();
                                                            }
                                                            bm = resized;

                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, tags, ubid);
                                                            items.add(item);
                                                            initView();
                                                        } else {
                                                            Thread bthread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        URL urll = new URL(url);
                                                                        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                        conn.connect();
                                                                        InputStream bis = conn.getInputStream();
                                                                        Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                        int height = bmm.getHeight();
                                                                        int width = bmm.getWidth();

                                                                        Bitmap resized = null;
                                                                        if(height>width){
                                                                            while(height>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        } else {
                                                                            while(width>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        }

                                                                        bm = resized;
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }); bthread.start();
                                                            try {
                                                                bthread.join();
                                                                //리스트뷰에 추가
                                                                item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                        review, redate_2, isbn, rate, bname, nname, tags, ubid);
                                                                items.add(item);
                                                                initView();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 없고 찜도서 있고 이미지 없다");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                                                Log.d(TAG,"친구 있고 리뷰 있고 태그 없고 찜 도서 없다!");

                                                //uid로 이미지 가져오기
                                                Retrofit retro_imgFirst = new Retrofit.Builder()
                                                        .baseUrl(retroBaseApiService.Base_URL)
                                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                                retroBaseApiService = retro_imgFirst.create(RetroBaseApiService.class);

                                                retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 없고 찜 도서 없고 이미지 있다!");

                                                        //서버에서 받아온 이미지 비트맵으로 변환
                                                        InputStream is = response.body().byteStream();
                                                        Bitmap bitmap_profile = BitmapFactory.decodeStream(is);
                                                        String tags = "";


                                                        if(url == null){
                                                            //기본 이미지 비트맵으로 변환
                                                            Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                            int height = bmm.getHeight();
                                                            int width = bmm.getWidth();

                                                            Bitmap resized = null;
                                                            while(height>70){
                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                height = resized.getHeight();
                                                                width = resized.getWidth();
                                                            }
                                                            bm = resized;

                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, tags, 0);
                                                            items.add(item);
                                                            initView();
                                                        } else {
                                                            Thread bthread = new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        URL urll = new URL(url);
                                                                        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                        conn.connect();
                                                                        InputStream bis = conn.getInputStream();
                                                                        Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                        int height = bmm.getHeight();
                                                                        int width = bmm.getWidth();

                                                                        Bitmap resized = null;
                                                                        if(height>width){
                                                                            while(height>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        } else {
                                                                            while(width>70){
                                                                                resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                                height = resized.getHeight();
                                                                                width = resized.getWidth();
                                                                            }
                                                                        }

                                                                        bm = resized;
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }); bthread.start();
                                                            try {
                                                                bthread.join();
                                                                //리스트뷰에 추가
                                                                item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                        review, redate_2, isbn, rate, bname, nname, tags, 0);
                                                                items.add(item);
                                                                initView();
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Log.d(TAG,"친구 있고 리뷰 있고 태그 없고 찜 도서 없고 이미지 없다!");
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<List<HomeData>> call, Throwable t) {
                            Log.d(TAG,"친구 있고 리뷰 없다!");
                            Toast.makeText(getActivity(), "리뷰 정보 없음.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }



            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {
                Log.d(TAG,"친구 없다!");

                //사용자 uid로 리뷰 가져오기
                Retrofit retro_info = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_info.create(RetroBaseApiService.class);

                retroBaseApiService.getHreq(useruid).enqueue(new Callback<List<HomeData>>() {
                    @Override
                    public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                        Log.d(TAG,"친구 없고 리뷰 있다!");

                        //리뷰 정보 가져오기
                        homeDataList = response.body();
                        items = new ArrayList<>();

                        for(int l = 0; l < homeDataList.size(); l++){
                            /**리뷰 정보에 ReviewUID 정보 추가해서 가져옴*/
                            int uid = homeDataList.get(l).getUserUID();
                            int ruid = homeDataList.get(l).getReviewUID();
                            String url = homeDataList.get(l).getBookImageUrl();
                            String review = homeDataList.get(l).getReview();
                            String redate = homeDataList.get(l).getReviewDate();
                            String isbn = homeDataList.get(l).getISBN13();
                            int rate = homeDataList.get(l).getRate();
                            String bname = homeDataList.get(l).getBookName();
                            String nname = homeDataList.get(l).getNickName();

                            //날짜 변환
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(redate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                            String redate_2 = sdf.format(date);

                            //ReviewUID로 Tag리스트 가져오기
                            Retrofit retro_info = new Retrofit.Builder()
                                    .baseUrl(retroBaseApiService.Base_URL)
                                    .addConverterFactory(GsonConverterFactory.create()).build();
                            retroBaseApiService = retro_info.create(RetroBaseApiService.class);

                            retroBaseApiService.getReviewtag(ruid).enqueue(new Callback<List<HomeData>>() {
                                @Override
                                public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 있다!");
                                    taglist = response.body();

                                    String tags = new String();
                                    for(int w =0; w<taglist.size(); w++){
                                        tags += "#" + taglist.get(w).getTag();
                                    }
                                    String secondTags = tags;

                                    //리뷰 도서 중 소유 도서에 저장된 정보 가져오기
                                    Retrofit retro_id = new Retrofit.Builder()
                                            .baseUrl(retroBaseApiService.Base_URL)
                                            .addConverterFactory(GsonConverterFactory.create()).build();
                                    retroBaseApiService = retro_id.create(RetroBaseApiService.class);

                                    retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
                                        @Override
                                        public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                                            Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 찜 도서 있다!");
                                            userbookUID = response.body();
                                            int bid = userbookUID.getUserBookUID();

                                            String finalTags = secondTags;

                                            //uid로 이미지 가져오기
                                            Retrofit retro_im = new Retrofit.Builder()
                                                    .baseUrl(retroBaseApiService.Base_URL)
                                                    .addConverterFactory(GsonConverterFactory.create()).build();
                                            retroBaseApiService = retro_im.create(RetroBaseApiService.class);

                                            retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 찜 도서 있고 이미지 있다!");

                                                    //서버에서 받아온 이미지 비트맵으로 변환
                                                    InputStream is = response.body().byteStream();
                                                    Bitmap bitmap_profile = BitmapFactory.decodeStream(is);


                                                    if(url == null){
                                                        //기본 이미지 비트맵으로 변환
                                                        Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                        int height = bmm.getHeight();
                                                        int width = bmm.getWidth();

                                                        Bitmap resized = null;
                                                        while(height>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                        bm = resized;

                                                        //리스트뷰에 추가
                                                        item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                review, redate_2, isbn, rate, bname, nname, finalTags, bid);
                                                        items.add(item);
                                                        initView();
                                                    } else {
                                                        Thread bthread = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    URL urll = new URL(url);
                                                                    HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                    conn.connect();
                                                                    InputStream bis = conn.getInputStream();
                                                                    Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                    int height = bmm.getHeight();
                                                                    int width = bmm.getWidth();

                                                                    Bitmap resized = null;
                                                                    if(height>width){
                                                                        while(height>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    } else {
                                                                        while(width>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    }

                                                                    bm = resized;
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }); bthread.start();
                                                        try {
                                                            bthread.join();
                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, finalTags, bid);
                                                            items.add(item);
                                                            initView();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 찜 도서 있고 이미지 없다!");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                                            Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 찜 도서 없다!");

                                            String finalTags = secondTags;

                                            //uid로 이미지 가져오기
                                            Retrofit retro_im = new Retrofit.Builder()
                                                    .baseUrl(retroBaseApiService.Base_URL)
                                                    .addConverterFactory(GsonConverterFactory.create()).build();
                                            retroBaseApiService = retro_im.create(RetroBaseApiService.class);

                                            retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 찜 도서 있고 이미지 있다!");
                                                    //서버에서 받아온 이미지 비트맵으로 변환
                                                    InputStream is = response.body().byteStream();
                                                    Bitmap bitmap_profile = BitmapFactory.decodeStream(is);


                                                    if(url == null){
                                                        //기본 이미지 비트맵으로 변환
                                                        Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                        int height = bmm.getHeight();
                                                        int width = bmm.getWidth();

                                                        Bitmap resized = null;
                                                        while(height>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                        bm = resized;

                                                        //리스트뷰에 추가
                                                        item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                review, redate_2, isbn, rate, bname, nname, finalTags, 0);
                                                        items.add(item);
                                                        initView();
                                                    } else {
                                                        Thread bthread = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    URL urll = new URL(url);
                                                                    HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                    conn.connect();
                                                                    InputStream bis = conn.getInputStream();
                                                                    Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                    int height = bmm.getHeight();
                                                                    int width = bmm.getWidth();

                                                                    Bitmap resized = null;
                                                                    if(height>width){
                                                                        while(height>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    } else {
                                                                        while(width>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    }

                                                                    bm = resized;
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }); bthread.start();
                                                        try {
                                                            bthread.join();
                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, finalTags, 0);
                                                            items.add(item);
                                                            initView();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 이미지 없다");
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<List<HomeData>> call, Throwable t) {
                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 없다!");

                                    //리뷰 도서 중 소유 도서에 저장된 정보 가져오기
                                    Retrofit retro_id = new Retrofit.Builder()
                                            .baseUrl(retroBaseApiService.Base_URL)
                                            .addConverterFactory(GsonConverterFactory.create()).build();
                                    retroBaseApiService = retro_id.create(RetroBaseApiService.class);

                                    retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
                                        @Override
                                        public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                                            Log.d(TAG,"친구 없고 리뷰 있고 태그 없고 찜 도서 있다!");
                                            userbookUID = response.body();
                                            int bid = userbookUID.getUserBookUID();

                                            //uid로 이미지 가져오기
                                            Retrofit retro_imgFi = new Retrofit.Builder()
                                                    .baseUrl(retroBaseApiService.Base_URL)
                                                    .addConverterFactory(GsonConverterFactory.create()).build();
                                            retroBaseApiService = retro_imgFi.create(RetroBaseApiService.class);

                                            retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 이미지 있다!");
                                                    //서버에서 받아온 이미지 비트맵으로 변환
                                                    InputStream is = response.body().byteStream();
                                                    Bitmap bitmap_profile = BitmapFactory.decodeStream(is);
                                                    String tags = "";

                                                    if(url == null){
                                                        //기본 이미지 비트맵으로 변환
                                                        Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                        int height = bmm.getHeight();
                                                        int width = bmm.getWidth();

                                                        Bitmap resized = null;
                                                        while(height>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                        bm = resized;

                                                        //리스트뷰에 추가
                                                        item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                review, redate_2, isbn, rate, bname, nname, tags, bid);
                                                        items.add(item);
                                                        initView();
                                                    } else {
                                                        Thread bthread = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    URL urll = new URL(url);
                                                                    HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                    conn.connect();
                                                                    InputStream bis = conn.getInputStream();
                                                                    Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                    int height = bmm.getHeight();
                                                                    int width = bmm.getWidth();

                                                                    Bitmap resized = null;
                                                                    if(height>width){
                                                                        while(height>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    } else {
                                                                        while(width>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    }

                                                                    bm = resized;
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }); bthread.start();
                                                        try {
                                                            bthread.join();
                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, tags, bid);
                                                            items.add(item);
                                                            initView();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 있고 이미지 없다");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                                            Log.d(TAG,"친구 없고 리뷰 있고 태그 없고 찜 도서 없다!");

                                            //uid로 이미지 가져오기
                                            Retrofit retro_imgFi = new Retrofit.Builder()
                                                    .baseUrl(retroBaseApiService.Base_URL)
                                                    .addConverterFactory(GsonConverterFactory.create()).build();
                                            retroBaseApiService = retro_imgFi.create(RetroBaseApiService.class);

                                            retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 없고 이미지 있다!");
                                                    //서버에서 받아온 이미지 비트맵으로 변환
                                                    InputStream is = response.body().byteStream();
                                                    Bitmap bitmap_profile = BitmapFactory.decodeStream(is);
                                                    String tags = "";


                                                    if(url == null){
                                                        //기본 이미지 비트맵으로 변환
                                                        Bitmap bmm = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_img);
                                                        int height = bmm.getHeight();
                                                        int width = bmm.getWidth();

                                                        Bitmap resized = null;
                                                        while(height>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                        bm = resized;

                                                        //리스트뷰에 추가
                                                        item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                review, redate_2, isbn, rate, bname, nname, tags, 0);
                                                        items.add(item);
                                                        initView();
                                                    } else {
                                                        Thread bthread = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    URL urll = new URL(url);
                                                                    HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                                    conn.connect();
                                                                    InputStream bis = conn.getInputStream();
                                                                    Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                                    int height = bmm.getHeight();
                                                                    int width = bmm.getWidth();

                                                                    Bitmap resized = null;
                                                                    if(height>width){
                                                                        while(height>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    } else {
                                                                        while(width>70){
                                                                            resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                                            height = resized.getHeight();
                                                                            width = resized.getWidth();
                                                                        }
                                                                    }

                                                                    bm = resized;
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }); bthread.start();
                                                        try {
                                                            bthread.join();
                                                            //리스트뷰에 추가
                                                            item = new HomeReviewItem(bitmap_profile, uid, ruid, url, bm,
                                                                    review, redate_2, isbn, rate, bname, nname, tags, 0);
                                                            items.add(item);
                                                            initView();
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.d(TAG,"친구 없고 리뷰 있고 태그 없고 이미지 없다");
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<HomeData>> call, Throwable t) {
                        Log.d(TAG,"친구 없고 리뷰 없다!");
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
        listview.setAdapter(hrAdapter);
    }
}