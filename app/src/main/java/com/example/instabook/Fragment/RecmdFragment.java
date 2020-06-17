package com.example.instabook.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.RecmdAdapter;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.R;

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
    List<RecmdBookItem> recmdlist;
    ArrayList<RecmdBookItem> items;
    UserBookUIDData userbookUID;
    RecmdBookItem rb;
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

        //유저 UID 가져오기
        final int useruid = sp.getUserUid(getActivity());
        Log.d(TAG,"현 사용자 uid  "+useruid);

        Retrofit retro_rcmd = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_rcmd.create(RetroBaseApiService.class);

        retroBaseApiService.getRcmd(useruid).enqueue(new Callback<List<RecmdBookItem>>() {
            @Override
            public void onResponse(Call<List<RecmdBookItem>> call, Response<List<RecmdBookItem>> response) {
                recmdlist = response.body();
                Log.d(TAG,"추천 도서 정보 응답 받음");
                items = new ArrayList<>();
                for(int i = 0; i < recmdlist.size(); i++) {
                    String b = recmdlist.get(i).getRbname();
                    String isbn = recmdlist.get(i).getRisbn();
                    String url = recmdlist.get(i).getRimguri();
                    String p = recmdlist.get(i).getRpub();

                    Retrofit retro_id = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_id.create(RetroBaseApiService.class);

                    retroBaseApiService.getUBid(useruid,isbn).enqueue(new Callback<UserBookUIDData>() {
                        @Override
                        public void onResponse(Call<UserBookUIDData> call, Response<UserBookUIDData> response) {
                            userbookUID = response.body();

                            int bid = userbookUID.getUserBookUID();
                            rb = new RecmdBookItem(b, isbn, url, p, bid);
                            items.add(rb);
                            Log.d(TAG,"찜 도서 정보 있음");
                            Log.d(TAG,"추천 도서 정보 items에 넣음");
                            initView();
                        }
                        @Override
                        public void onFailure(Call<UserBookUIDData> call, Throwable t) {
                            rb = new RecmdBookItem(b, isbn, url, p, 0);
                            items.add(rb);
                            Log.d(TAG, "찜 도서 정보 없음 ");
                            Log.d(TAG,"추천 도서 정보 items에 넣음");
                            initView();
                        }
                    });

                }
                initView();
            }
            @Override
            public void onFailure(Call<List<RecmdBookItem>> call, Throwable t) {

            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initView() {
        RecmdAdapter rAdapter = new RecmdAdapter(getActivity(),
                R.layout.listview_recmd, items);
        ListView listview = (ListView) getActivity().findViewById(R.id.recom_listview);
        Log.d(TAG,"추천 도서 정보 어댑터 선언");

        listview.setAdapter(rAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //rAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
