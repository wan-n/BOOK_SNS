package com.example.instabook.Activity.ForBook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.ForReview.ReviewActivity;
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
    String author;
    String keyword;

    ArrayList<SearchBookItem> books;
    List<BookData> bookDataList;
    List<BookData> authorDataList;
    SearchBookItem mb;

    RetroBaseApiService retroBaseApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchsub);

        Intent intent = getIntent(); //데이터 수신
        keyword = intent.getStringExtra("keyword"); //intent 값을 String 타입으로 변환
        Log.d(TAG, "키워드: " + keyword);

        getBook(keyword);
    }

    public void getBook(String keyword){
        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.getBook(keyword).enqueue(new Callback<List<BookData>>() {
            @Override
            public void onResponse(Call<List<BookData>> call, Response<List<BookData>> response) {
                bookDataList = response.body();
                books = new ArrayList<>();

                for(int i = 0; i < bookDataList.size(); i++){
                    String t = bookDataList.get(i).getTitle();
                    String p = bookDataList.get(i).getPublisher();
                    String is = bookDataList.get(i).getIsbn();
                    /**도서 이미지*/

                    mb = new SearchBookItem(t, is, p);
                    books.add(mb);
                }

                BookListAdapter blAdapter = new BookListAdapter(getApplicationContext(),
                        R.layout.listview_searchbook, books);

                ListView listview = (ListView) findViewById(R.id.listview);
                listview.setAdapter(blAdapter);
                /*
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String t = books.get(position).title;
                        String is = books.get(position).isbn;

                        Intent intent = new Intent(SearchdbActivity.this, ReviewActivity.class);
                        intent.putExtra("title",t);  //Intent는 데이터를 extras 키-값 쌍으로 전달
                        intent.putExtra("isbn", is);
                        startActivity(intent);
                    }
                });
                 */
            }

            @Override
            public void onFailure(Call<List<BookData>> call, Throwable t) {
                Intent intent = new Intent(getApplicationContext(), SearchSubActivity.class);
                intent.putExtra("keyword", keyword);  //Intent는 데이터를 extras 키-값 쌍으로 전달
                startActivity(intent);
            }
        });
    }
/**TODO 저자 정보 저장 구현
 public String getAuthor(String isbn){
 Retrofit retro_name = new Retrofit.Builder()
 .baseUrl(retroBaseApiService.Base_URL)
 .addConverterFactory(GsonConverterFactory.create()).build();
 retroBaseApiService = retro_name.create(RetroBaseApiService.class);

 retroBaseApiService.getAuthor(isbn).enqueue(new Callback<List<BookData>>() {
@Override
public void onResponse(Call<List<BookData>> call, Response<List<BookData>> response) {
authorDataList = response.body();

for(int j = 0; j<authorDataList.size(); j++){
author += authorDataList.get(j).getAuthor();
}
Log.d(TAG, "책 저자: " + author);
}

@Override
public void onFailure(Call<List<BookData>> call, Throwable t) {

}
});
 return  author;
 }
 */
}