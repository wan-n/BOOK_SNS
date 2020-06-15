package com.example.instabook.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.BookListAdapter;
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
    private static final String TAG = "HomeFragment";
    List<RecmdBookItem> recmdlist;
    ArrayList<RecmdBookItem> items;
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
        recmdlist = new ArrayList<>();
        Retrofit retro_rem = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_rem.create(RetroBaseApiService.class);
        retroBaseApiService.getRcmd().enqueue(new Callback<List<RecmdBookItem>>() {
            @Override
            public void onResponse(Call<List<RecmdBookItem>> call, Response<List<RecmdBookItem>> response) {
                recmdlist = response.body();
                items = new ArrayList<>();

                for(int i = 0; i < recmdlist.size(); i++){
                    String b = recmdlist.get(i).getRbname();
                    String isbn = recmdlist.get(i).getRisbn();
                    String url = recmdlist.get(i).getRimguri();
                    String p = recmdlist.get(i).getRpub();

                    rb = new RecmdBookItem(b,isbn,url,p);
                    items.add(rb);
                }
                RecmdAdapter rAdapter = new RecmdAdapter(getActivity(),
                        R.layout.listview_searchbook, items);

                ListView listview = (ListView) getActivity().findViewById(R.id.listview);
                listview.setAdapter(rAdapter);

            }

            @Override
            public void onFailure(Call<List<RecmdBookItem>> call, Throwable t) {

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

}
