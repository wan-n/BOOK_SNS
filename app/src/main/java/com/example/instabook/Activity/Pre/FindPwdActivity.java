package com.example.instabook.Activity.Pre;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.instabook.R;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindPwdActivity extends AppCompatActivity {
    private EditText findpwd_id, findpwd_answer;
    private Button findpwd_go;
    private Spinner findpwd_question;
    private ImageView findpwd_back;
    int question;

    public RetroBaseApiService retroBaseApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);

        findpwd_id = findViewById(R.id.findpwd_id);
        findpwd_answer = findViewById(R.id.findpwd_answer);
        findpwd_go = findViewById(R.id.findpwd_go);
        findpwd_question = findViewById(R.id.findpwd_question);
        findpwd_back = findViewById(R.id.findpwd_back);

        //스피너 선택값 받아오기
        findpwd_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question = position + 1;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.findpwd_go:
                        String user_id = findpwd_id.getText().toString();
                        String question_answer = findpwd_answer.getText().toString();

                        final String userid = user_id.trim();

                        final String answer = question_answer.trim();

                        //빈칸으로 제출했을 경우
                        if(userid.getBytes().length <= 0 || answer.getBytes().length <= 0){
                            Toast.makeText(getApplicationContext(), "빈 칸에 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        //모두 입력했을 경우
                        else {
                            //GET 으로 회원정보 가져오기
                            final Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(retroBaseApiService.Base_URL)
                                    .addConverterFactory(GsonConverterFactory.create()).build();
                            retroBaseApiService = retrofit.create(RetroBaseApiService.class);

                            retroBaseApiService.getPwd(userid).enqueue(new Callback<List<ResponseGet>>() {
                                @Override
                                public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {

                                    List<ResponseGet> userdatas = response.body();
                                    int res_questionuid = userdatas.get(0).getQeustionUID();
                                    String res_answer = userdatas.get(0).getAnswer();

                                    //올바르게 입력했을 경우
                                    if(res_questionuid == question && res_answer.equals(answer)){

                                        Intent change = new Intent(getApplicationContext(), ChangePwdActivity.class);
                                        change.putExtra("id", userid);
                                        startActivity(change);

                                    }

                                    else if(res_questionuid != question || !res_answer.equals(answer)){
                                        Toast.makeText(getApplicationContext(), "올바른 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                //DB에서 정보를 찾을 수 없을 경우
                                @Override
                                public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case R.id.findpwd_back:  //뒤로가기
                        onBackPressed();
                        break;
                }
            }
        };
        findpwd_go.setOnClickListener(listener);
        findpwd_back.setOnClickListener(listener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
