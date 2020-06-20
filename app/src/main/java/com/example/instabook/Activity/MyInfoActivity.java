package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.Dialog.EmailDialog;
import com.example.instabook.Activity.Dialog.NicknameDialog;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Fragment.InfoFragment;
import com.example.instabook.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {

    TextView minfo_id, minfo_email, mnickname_id;
    ImageView minfo_back;
    FrameLayout minfo_fr_back, edit_email, edit_nickname;

    RetroBaseApiService retroBaseApiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);



        //유저 아이디, UID, 닉네임 가져오기
        final String userid = SaveSharedPreference.getUserName(MyInfoActivity.this);
        final String useremail = SaveSharedPreference.getUserEmail(MyInfoActivity.this);
        final String username = SaveSharedPreference.getUserNickname(MyInfoActivity.this);

        minfo_back = findViewById(R.id.minfo_back);
        minfo_id = findViewById(R.id.minfo_id);
        minfo_email = findViewById(R.id.minfo_email);
        minfo_fr_back = findViewById(R.id.minfo_fr_back);
        mnickname_id = findViewById(R.id.mnickname_id);
        edit_email = findViewById(R.id.edit_email);
        edit_nickname = findViewById(R.id.edit_nickname);

        edit_email.setOnClickListener(this);
        edit_nickname.setOnClickListener(this);


        //화면에 유저정보 출력
        minfo_id.setText(userid);
        minfo_email.setText(useremail);
        mnickname_id.setText(username);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //뒤로가기
                    case R.id.minfo_fr_back:
                        onBackPressed();
                        break;
                }
            }
        };
        minfo_fr_back.setOnClickListener(listener);

    }

    //변경할 닉네임 중복 확인
    private void conNickName(String name, String id){

        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.getEditname(name).enqueue(new Callback<List<ResponseGet>>() {

            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                List<ResponseGet> get_name = response.body();
                if(name.equals(get_name.get(0).getNickName()))
                    Toast.makeText(MyInfoActivity.this, "중복된 닉네임 입니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                changeNickName(name, id);
            }
        });
    }

    //닉네임 변경
    private void changeNickName(String name, String id){
        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.putName(id, name).enqueue(new Callback<ResponseGet>() {
            @Override
            public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {

                SaveSharedPreference.setUserNickName(MyInfoActivity.this, name);
                Toast.makeText(MyInfoActivity.this, "변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                //새로고침
                Intent in = new Intent(getApplicationContext(), MyInfoActivity.class);
                startActivity(in);
                finish();

            }

            @Override
            public void onFailure(Call<ResponseGet> call, Throwable t) {
                Toast.makeText(MyInfoActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //닉네임 변경
            case R.id.edit_nickname:

                NicknameDialog ndialog = new NicknameDialog(MyInfoActivity.this);
                ndialog.setDialogListener(new NicknameDialog.NicknameDialogListener() {

                    @Override
                    public void onPositiveClicked(String nickname) {
                        Log.d("NICKNAME", "닉네임 : "+nickname);
                        String userid = SaveSharedPreference.getUserName(MyInfoActivity.this);

                        if(nickname.length() <= 0){
                            Toast.makeText(MyInfoActivity.this, "변경할 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        } else if(nickname.length() > 10){
                            Toast.makeText(MyInfoActivity.this, "10글자 이내로만 변경 가능합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            conNickName(nickname, userid);
                        }
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                ndialog.show();
                break;
            //이메일 변경
            case R.id.edit_email:
                EmailDialog edialog = new EmailDialog(this);
                edialog.setDialogListener(new EmailDialog.EmailDialogListener() {
                    @Override
                    public void onPositiveClicked(String email) {
                        int userid = SaveSharedPreference.getUserUid(MyInfoActivity.this);

                        Retrofit retro_getpub = new Retrofit.Builder()
                                .baseUrl(retroBaseApiService.Base_URL)
                                .addConverterFactory(GsonConverterFactory.create()).build();
                        retroBaseApiService = retro_getpub.create(RetroBaseApiService.class);

                        retroBaseApiService.putEmail(userid, email).enqueue(new Callback<ResponseGet>() {
                            @Override
                            public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                                SaveSharedPreference.setUserEmail(MyInfoActivity.this, email);
                                Intent intent = new Intent(getApplicationContext(), MyInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<ResponseGet> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNegativeClicked() {
                    }
                });
                edialog.show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

}
