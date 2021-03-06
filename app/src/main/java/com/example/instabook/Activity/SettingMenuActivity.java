package com.example.instabook.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instabook.Activity.Dialog.LogoutDialog;
import com.example.instabook.Activity.Dialog.ProfileDialog;
import com.example.instabook.Activity.Dialog.WithdrawalDialog;
import com.example.instabook.Activity.Pre.FindIdActivity;
import com.example.instabook.Activity.Pre.LoginActivity;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView set_back;
    private TextView set_info, set_friends, set_logout, set_withdrawal;
    private Switch set_pub;
    FrameLayout set_fr_logout, set_fr_withdrawal, set_fr_back;

    LinearLayout set_fr_info, set_fr_friends;

    RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingmenu);



        set_back = findViewById(R.id.set_back);
        set_info = findViewById(R.id.set_info);
        set_friends = findViewById(R.id.set_friends);
        set_logout = findViewById(R.id.set_logout);
        set_withdrawal = findViewById(R.id.set_withdrawal);
        set_pub = findViewById(R.id.set_pub);
        set_fr_info = findViewById(R.id.set_fr_info);
        set_fr_friends = findViewById(R.id.set_fr_friends);
        set_fr_logout = findViewById(R.id.set_fr_logout);
        set_fr_withdrawal = findViewById(R.id.set_fr_withdrawal);
        set_fr_back = findViewById(R.id.set_fr_back);

        //커스텀 다이얼로그 이벤트 연결
        set_fr_logout.setOnClickListener(this);
        set_fr_withdrawal.setOnClickListener(this);


        //유저 아이디, UID 가져오기
        final String userid = sp.getUserName(SettingMenuActivity.this);
        final int useruid = sp.getUserUid(SettingMenuActivity.this);


        //프로필 공개 스위치 초기상태 불러오기
        Retrofit retro_getpub = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_getpub.create(RetroBaseApiService.class);

        retroBaseApiService.getPub(userid).enqueue(new Callback<List<ResponseGet>>() {
            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                List<ResponseGet> user_pub = response.body();
                if(user_pub.get(0).getIsPublic() == 1){
                    set_pub.setChecked(true);
                }else if(user_pub.get(0).getIsPublic() == 0){
                    set_pub.setChecked(false);
                }else{
                    Toast.makeText(getApplicationContext(), "회원 정보에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "다시 로드해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        //프로필 공개 스위치 이벤트
        set_pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(set_pub.isChecked()){  //스위치 클릭 시 ON 상태이면

                    Retrofit retro_puton = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_puton.create(RetroBaseApiService.class);

                    retroBaseApiService.putPub(userid, 1).enqueue(new Callback<ResponseGet>() {
                        @Override
                        public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                            //ON 상태
                        }
                        @Override
                        public void onFailure(Call<ResponseGet> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "서버 오류", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{   //스위치 클릭 시 OFF 상태이면

                    Retrofit retro_putoff = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_putoff.create(RetroBaseApiService.class);

                    retroBaseApiService.putPub(userid, 0).enqueue(new Callback<ResponseGet>() {
                        @Override
                        public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                            //OFF 상태
                        }
                        @Override
                        public void onFailure(Call<ResponseGet> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "서버 오류", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });



        View.OnClickListener listener = new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //뒤로가기
                    case R.id.set_fr_back:
                        onBackPressed();
                        break;
                    //내정보
                    case R.id.set_fr_info:
                        intent = new Intent(getApplicationContext(), MyInfoActivity.class);
                        startActivity(intent);
                        break;
                    //친구 관리
                    case R.id.set_fr_friends:
                        intent = new Intent(getApplicationContext(), FriendsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        set_fr_back.setOnClickListener(listener);
        set_fr_info.setOnClickListener(listener);
        set_fr_friends.setOnClickListener(listener);

    }

    //뒤로가기
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //로그아웃
            case R.id.set_fr_logout:
                LogoutDialog ldialog = new LogoutDialog(SettingMenuActivity.this);
                ldialog.setDialogListener(new LogoutDialog.LogoutDialogListener() {
                    @Override
                    public void onPositiveClicked() {
                        //로그아웃시 쉐어드 프리퍼런스에서 유저 정보 지움
                        SaveSharedPreference.clearUserName(SettingMenuActivity.this);
                        Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_SHORT).show();

                        //로그인 화면으로 되돌아감
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNegativeClicked() {
                    }
                });
                ldialog.show();
                break;
             //회원탈퇴
            case R.id.set_fr_withdrawal:
                String userid = SaveSharedPreference.getUserName(SettingMenuActivity.this);
                int useruid = SaveSharedPreference.getUserUid(SettingMenuActivity.this);

                WithdrawalDialog wdialog = new WithdrawalDialog(SettingMenuActivity.this);
                wdialog.setDialogListener(new WithdrawalDialog.WithdrawalDialogListener() {
                    @Override
                    public void onPositiveClicked() {
                        Retrofit retro_del = new Retrofit.Builder()
                                .baseUrl(retroBaseApiService.Base_URL)
                                .addConverterFactory(GsonConverterFactory.create()).build();
                        retroBaseApiService = retro_del.create(RetroBaseApiService.class);

                        retroBaseApiService.putInfo(userid).enqueue(new Callback<ResponseGet>() {
                            @Override
                            public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {

                                //서버에 저장된 프로필 이미지 제거
                                retroBaseApiService.delImage(useruid).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        Toast.makeText(getApplicationContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                    }
                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(SettingMenuActivity.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                //sharedpreference 회원정보 제거
                                SaveSharedPreference.clearUserName(SettingMenuActivity.this);
                                //로그인 화면으로 되돌아감
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
                wdialog.show();
                break;
        }

    }
}
