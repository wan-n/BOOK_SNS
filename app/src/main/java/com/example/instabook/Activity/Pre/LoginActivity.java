package com.example.instabook.Activity.Pre;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    private EditText login_id, login_pw;
    private Button go_reg, login_ok;
    private TextView go_find;

    public RetroBaseApiService retroBaseApiService;
    private static String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        go_reg = findViewById(R.id.go_reg);
        login_ok = findViewById(R.id.login_ok);
        go_find = findViewById(R.id.go_find);

        //버튼 리스너 생성
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아이디를 가져와 클릭된 뷰에대한 처리를 한다
                switch (v.getId()){
                    case R.id.go_reg:  //회원가입 버튼
                        Intent reg = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(reg);
                        break;
                    case R.id.login_ok:  //로그인 버튼
                        String userid = login_id.getText().toString();
                        String userpwd = login_pw.getText().toString();

                        final String id = userid.trim();
                        final String pwd = userpwd.trim();

                        //빈칸으로 제출했을 경우
                        if(userid.getBytes().length <= 0 || userpwd.getBytes().length <= 0){
                            Toast.makeText(getApplicationContext(), "빈 칸에 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        //모두 입력했을 경우
                        else {
                            //GET 으로 회원정보 가져오기
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(retroBaseApiService.Base_URL)
                                    .addConverterFactory(GsonConverterFactory.create()).build();
                            retroBaseApiService = retrofit.create(RetroBaseApiService.class);

                            retroBaseApiService.getLogin(id).enqueue(new Callback<List<ResponseGet>>() {
                                @Override
                                public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {

                                        List<ResponseGet> userdatas = response.body();
                                        int uid = userdatas.get(0).getUserUID();
                                        String email = userdatas.get(0).getEmail();
                                        String nickname = userdatas.get(0).getNickName();

                                        if (id.equals(userdatas.get(0).getUserId()) && pwd.equals(userdatas.get(0).getPassword())) {
                                            //로그인 완료
                                            Toast.makeText(getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                            //쉐어드프리퍼런스에 유저아이디, 유저UID, email 저장(이후엔 자동로그인)
                                            SaveSharedPreference.setUserName(LoginActivity.this, id, uid, email, nickname);

                                            //프로필 이미지도 저장
                                            Retrofit retro_img = new Retrofit.Builder()
                                                    .baseUrl(retroBaseApiService.Base_URL)
                                                    .addConverterFactory(GsonConverterFactory.create()).build();
                                            retroBaseApiService = retro_img.create(RetroBaseApiService.class);

                                            retroBaseApiService.getImage(uid).enqueue(new Callback<ResponseBody>() {

                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    //서버에서 받아온 이미지 비트맵으로 변환
                                                    assert response.body() != null;
                                                    InputStream is = response.body().byteStream();
                                                    Bitmap bitmap_profile = BitmapFactory.decodeStream(is);

                                                    //비트맵을 문자열로 변환
                                                    String string_profile = bitMapToString(bitmap_profile);

                                                    //쉐어드프리퍼런스에 저장
                                                    SaveSharedPreference.setUserImage(LoginActivity.this, string_profile);

                                                    //Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    //Toast.makeText(getApplicationContext(), "불러올 이미지 없음", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                           //홈탭으로 이동한다.
                                            Intent Success = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(Success);

                                        }
                                        //아이디||비밀번호가 올바르지 않을 경우
                                        else if (!id.equals(userdatas.get(0).getUserId()) || !pwd.equals(userdatas.get(0).getPassword())) {
                                            Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                }
                                //DB에서 검색 결과가 없을 경우
                                @Override
                                public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case R.id.go_find:  //아이디/비밀번호 찾기
                        Intent findid = new Intent(getApplicationContext(), FindIdActivity.class);
                        startActivity(findid);
                        break;
                }
            }
        };

        //버튼을 누르면 리스너의 onClick이 호출
        go_reg.setOnClickListener(listener);
        login_ok.setOnClickListener(listener);
        go_find.setOnClickListener(listener);
    }


    //뒤로가기 2번 클릭 시 종료
    private long lastTimeBackPressed; //뒤로가기 버튼이 클릭된 시간
    @Override
    public void onBackPressed()
    {

        //2초 이내에 뒤로가기 버튼을 재 클릭 시 앱 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 2000)
        {
            finish();
            return;
        }
        //'뒤로' 버튼 한번 클릭 시 메시지
        Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        //lastTimeBackPressed에 '뒤로'버튼이 눌린 시간을 기록
        lastTimeBackPressed = System.currentTimeMillis();
    }


    //프로필 이미지를 문자열로 Sharedpreference에 저장
    public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b,Base64.DEFAULT);
        return temp;
    }


}
