package com.example.instabook.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.instabook.Activity.ForBook.SearchdbActivity;
import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.ForTag.SearchTagActivity;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.RecmdAdapter;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;


public class RecmdFragment extends Fragment {
    private static final String TAG = "RecmdFragment";
    Button sbtn;
    EditText stxt;

    List<RecmdBookItem> recmdlist;
    ArrayList<RecmdBookItem> items;
    UserBookUIDData userbookUID;
    RecmdBookItem rb;
    SaveSharedPreference sp;
    View rootView;
    Bitmap bp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recmd, container, false);

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(getActivity());

        //추천 도서 목록 가져오기
        Retrofit retro_rcmd = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_rcmd.create(RetroBaseApiService.class);

        retroBaseApiService.getRcmd(useruid).enqueue(new Callback<List<RecmdBookItem>>() {
            @Override
            public void onResponse(Call<List<RecmdBookItem>> call, Response<List<RecmdBookItem>> response) {
                recmdlist = response.body();

                items = new ArrayList<>();
                for(int i = 0; i < recmdlist.size(); i++) {
                    String b = recmdlist.get(i).getRbname();
                    String isbn = recmdlist.get(i).getRisbn();
                    String url = recmdlist.get(i).getRimguri();
                    String p = recmdlist.get(i).getRpub();

                    //추천 도서 중 소유 도서에 저장된 정보 가져오기
                    Retrofit retro_id = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_id.create(RetroBaseApiService.class);

                    retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
                        @Override
                        public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                            userbookUID = response.body();
                            int bid = userbookUID.getUserBookUID();

                            //이미지 url 비트맵으로 변환
                            int idx = url.indexOf("?");
                            String imgurl = url.substring(0, idx);
                            Thread uthread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL url = new URL(imgurl);
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.connect();
                                        InputStream bis = conn.getInputStream();
                                        Bitmap bmm = BitmapFactory.decodeStream(bis);

                                        int height = bmm.getHeight();
                                        int width = bmm.getWidth();

                                        Bitmap resized = null;
                                        while(height>70){
                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                            height = resized.getHeight();
                                            width = resized.getWidth();
                                        }
                                        bp = resized;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }); uthread.start();

                            try {
                                uthread.join();
                                rb = new RecmdBookItem(b, isbn, bp, p, bid);
                                items.add(rb);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            initView();
                        }
                        @Override
                        public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                            //이미지 url 비트맵으로 변환
                            int idx = url.indexOf("?");
                            String imgurl = url.substring(0, idx);
                            Thread uthread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL url = new URL(imgurl);
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.connect();
                                        InputStream bis = conn.getInputStream();
                                        Bitmap bmm = BitmapFactory.decodeStream(bis);

                                        int height = bmm.getHeight();
                                        int width = bmm.getWidth();

                                        Bitmap resized = null;
                                        while(height>70){
                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                            height = resized.getHeight();
                                            width = resized.getWidth();
                                        }
                                        bp = resized;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }); uthread.start();

                            try {
                                uthread.join();
                                rb = new RecmdBookItem(b, isbn, bp, p, 0);
                                items.add(rb);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            initView();
                        }
                    });

                }
                initView();
            }
            @Override
            public void onFailure(Call<List<RecmdBookItem>> call, Throwable t) {
                Log.d(TAG,"추천 도서 정보 없음");
            }
        });

        return rootView;
    }

    private void initView() {
        RecmdAdapter rAdapter = new RecmdAdapter(getActivity(),
                R.layout.listview_recmd, items);
        ListView listview = (ListView) getActivity().findViewById(R.id.recom_listview);

        listview.setAdapter(rAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sbtn = (Button) getView().findViewById(R.id.searchBookBtn);

        sbtn.setOnClickListener((View.OnClickListener) v -> {

            stxt = (EditText) getView().findViewById(R.id.searchtag);
            String inputkey = stxt.getText().toString(); //editView의 텍스트를 String으로 keyword에 저장
            String key = inputkey.trim();

            //intent로 데이터 전달
            Intent intent = new Intent(getActivity(), SearchTagActivity.class);
            intent.putExtra("keyword",key);  //Intent는 데이터를 extras 키-값 쌍으로 전달
            startActivity(intent);
        });
    }
}
