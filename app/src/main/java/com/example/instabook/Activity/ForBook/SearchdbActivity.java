package com.example.instabook.Activity.ForBook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.BookListAdapter;
import com.example.instabook.ListView.SearchBookItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchdbActivity extends AppCompatActivity {
    private static final String TAG = "SearchdbActivity";

    String keyword;

    ArrayList<SearchBookItem> books;
    SearchBookItem mb;
    SaveSharedPreference sp;
    RetroBaseApiService retroBaseApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchdb);

        Intent intent = getIntent(); //데이터 수신
        keyword = intent.getStringExtra("keyword"); //intent 값을 String 타입으로 변환
        Log.d(TAG, "키워드: " + keyword);

        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.getBook(keyword).enqueue(new Callback<List<BookData>>() {

            @Override
            public void onResponse(Call<List<BookData>> call, Response<List<BookData>> response) {
                List<BookData> booklist = response.body();
                books = new ArrayList<>();

                for(int i = 0; i < booklist.size(); i++){
                    String title = booklist.get(i).getTitle();
                    String isbn = booklist.get(i).getIsbn();
                    String pub = booklist.get(i).getPublisher();
                    Log.d(TAG, "책 제목: " + title);

                    getISBN(title,isbn, pub);

                }
                BookListAdapter blAdapter = new BookListAdapter(SearchdbActivity.this,
                        R.layout.listview_searchbook, books);

                ListView listview = (ListView) findViewById(R.id.listview);
                listview.setAdapter(blAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<BookData>> call, Throwable t) {
                Toast.makeText(SearchdbActivity.this, "페이지를 다시 로드해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getISBN(String title, String isbn, String pub){
        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.getAuthor(isbn).enqueue(new Callback<BookData>() {
            @Override
            public void onResponse(Call<BookData> call, Response<BookData> response) {
                BookData adata = response.body();
                    String author = adata.getAuthor();
                    Log.d(TAG, "책 저자: " + author);

                    mb = new SearchBookItem(title, author, pub);
                    books.add(mb);
            }

            @Override
            public void onFailure(Call<BookData> call, Throwable t) {
                Toast.makeText(SearchdbActivity.this, "저자 불러오기 실패.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}