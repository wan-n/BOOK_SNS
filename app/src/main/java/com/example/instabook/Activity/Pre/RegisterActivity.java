package com.example.instabook.Activity.Pre;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.instabook.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity {
    private EditText reg_id, reg_pw, reg_pwcon, reg_email, reg_answer;
    private Spinner reg_question;
    private Button reg_ok, reg_idcon;
    private ImageView reg_back;
    int question;
    Boolean check = false;

    public static RetroBaseApiService retroBaseApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //아이디 값 찾아주기
        reg_id = findViewById(R.id.reg_id);
        reg_pw = findViewById(R.id.reg_pw);
        reg_pwcon = findViewById(R.id.reg_pwcon);
        reg_email = findViewById(R.id.reg_email);
        reg_answer = findViewById(R.id.reg_answer);
        reg_question = findViewById(R.id.reg_question);
        reg_ok = findViewById(R.id.reg_ok);
        reg_back = findViewById(R.id.reg_back);
        reg_idcon = findViewById(R.id.reg_idcon);

        //스피너 아이템 번호 받아오기(질문UID 값)
        reg_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                String id = reg_id.getText().toString();
                String pwd = reg_pw.getText().toString();
                String con_pwd = reg_pwcon.getText().toString();
                String useremail = reg_email.getText().toString();
                String questionanswer = reg_answer.getText().toString();

                //입력값 좌우 공백 제거
                final String userid = id.trim();
                final String userpwd = pwd.trim();
                final String conpwd = con_pwd.trim();
                final String email = useremail.trim();
                final String answer = questionanswer.trim();

                switch (v.getId())
                {
                    case R.id.reg_idcon:  //아이디 중복확인 버튼
                        Retrofit dup_retro = new Retrofit.Builder()
                                .baseUrl(retroBaseApiService.Base_URL)
                                .addConverterFactory(GsonConverterFactory.create()).build();
                        retroBaseApiService = dup_retro.create(RetroBaseApiService.class);

                        retroBaseApiService.getDup(userid).enqueue(new Callback<List<ResponseGet>>() {
                            @Override
                            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                                List<ResponseGet> dup_id = response.body();

                                if(userid.equals(dup_id.get(0).getUserId())) {
                                    AlertDialog.Builder alarm = new AlertDialog.Builder(RegisterActivity.this);
                                    alarm.setTitle("알림");
                                    alarm.setMessage("중복된 아이디입니다.");
                                    alarm.setNeutralButton("뒤로가기", null);
                                    alarm.create().show();
                                    check = false;
                                }
                            }

                            @Override
                            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                                AlertDialog.Builder incorrect = new AlertDialog.Builder(RegisterActivity.this);
                                incorrect.setTitle("알림");
                                incorrect.setMessage("이 아이디를 사용하실 수 있습니다.");
                                incorrect.setNeutralButton("뒤로가기", null);
                                incorrect.create().show();
                                check = true;
                            }
                        });
                        break;
                    case R.id.reg_ok:   //회원가입 버튼
                        //아이디 중복확인을 완료했을 경우
                        if(check == true) {
                            //빈칸으로 제출했을 경우
                            if (userid.getBytes().length <= 0 || userpwd.getBytes().length <= 0 || conpwd.getBytes().length <= 0 ||
                                    email.getBytes().length <= 0 || answer.getBytes().length <= 0) {
                                Toast.makeText(getApplicationContext(), "빈 칸에 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            //비밀번호와 비밀번호 확인값이 다를 경우
                            else if (!userpwd.equals(conpwd)) {
                                Toast.makeText(getApplicationContext(), "비밀번호 확인에 올바른 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();

                            }
                            //모든 조건을 만족했을 경우
                            else if (userpwd.equals(conpwd)) {
                                //입력받은 정보 해시맵으로 받기
                                HashMap<String, Object> userinfo = new HashMap<>();
                                userinfo.put("id", userid);
                                userinfo.put("pwd", userpwd);
                                userinfo.put("email", email);
                                userinfo.put("pwdQuestionUID", question);
                                userinfo.put("pwdAnswer", answer);


                                //retrofit 초기 설정
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(retroBaseApiService.Base_URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();   //gson converter 생성
                                retroBaseApiService = retrofit.create(RetroBaseApiService.class);

                                retroBaseApiService.postReg(userinfo).enqueue(new Callback<ResponseGet>() {
                                    @Override
                                    public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                                        if (response.isSuccessful()) {  //통신이 성공적으로 완료되었다면 응답받을 내용
                                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                            //로그인 화면으로 돌아간다.
                                            Intent finish = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(finish);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseGet> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "통신오류", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            // 아이디 중복확인을 안했을 경우
                        } else if(check == false){
                            Toast.makeText(getApplicationContext(), "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.reg_back:  //뒤로가기 (로그인 화면으로 이동)
                        Intent back = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(back);
                        break;
                }
            }
        };

        //버튼을 누르면 리스너의 onClick이 호출
        reg_idcon.setOnClickListener(listener);
        reg_ok.setOnClickListener(listener);
        reg_back.setOnClickListener(listener);

    }
}

