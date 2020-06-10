package com.example.instabook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.ForBook.BookData;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity {

    private RetroBaseApiService retroBaseApiService;

    Button buttonEvent;
    EditText sendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        buttonEvent = (Button)findViewById(R.id.write_review);
        sendData = (EditText)findViewById(R.id.book_tag);

        buttonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, RsubActivity.class);
                intent.putExtra("sendData",sendData.getText().toString());
                startActivity(intent);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();   //gson converter 생성
                retroBaseApiService = retrofit.create(RetroBaseApiService.class);

                String tag = sendData.getText().toString();
                Toast.makeText(getApplicationContext(), tag, Toast.LENGTH_SHORT).show();

                retroBaseApiService.postTag(tag).enqueue(new Callback<BookData>() {
                    @Override
                    public void onResponse(Call<BookData> call, Response<BookData> response) {
                    }

                    @Override
                    public void onFailure(Call<BookData> call, Throwable t) {

                    }
                });
            }
        });


    }
}

