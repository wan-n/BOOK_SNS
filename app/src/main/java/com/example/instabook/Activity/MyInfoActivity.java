package com.example.instabook.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MyInfoActivity extends AppCompatActivity {

    TextView minfo_id, minfo_email;
    ImageView minfo_back;
    FrameLayout minfo_fr_back;

    RetroBaseApiService retroBaseApiService;
    SaveSharedPreference sp;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FrameLayout btn_facebook_logout, btn_facebook;
    private TextView tv_facebook, tv_facebook_logout;

    private Context mContext;
    private LoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        //유저 아이디, UID, 닉네임 가져오기
        final String userid = sp.getUserName(MyInfoActivity.this);
        final String useremail = sp.getUserEmail(MyInfoActivity.this);


        minfo_back = findViewById(R.id.minfo_back);
        minfo_id = findViewById(R.id.minfo_id);
        minfo_email = findViewById(R.id.minfo_email);
        minfo_fr_back = findViewById(R.id.minfo_fr_back);

        //화면에 유저정보 출력
        minfo_id.setText(userid);
        minfo_email.setText(useremail);




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




        //SNS 연동 관리
        mContext = getApplicationContext();

        tv_facebook = findViewById(R.id.tv_facebook);
        tv_facebook_logout = findViewById(R.id.tv_facebook_logout);
        btn_facebook_logout = findViewById(R.id.btn_facebook_logout);
        btn_facebook = findViewById(R.id.btn_facebook);

        //로그인 상태 확인(true ro false)
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn == true){
            btn_facebook.setEnabled(false);
            tv_facebook.setText("페이스북 연동됨");
            tv_facebook_logout.setText("연동 해제");
            onResume();
        }else{
            btn_facebook_logout.setEnabled(false);
            tv_facebook.setText("페이스북 연동하기");
            tv_facebook_logout.setText("");
            onResume();
        }

        //로그인
        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new LoginCallback();

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logInWithReadPermissions(MyInfoActivity.this,
                        Arrays.asList("public_profile", "email"));
                loginManager.registerCallback(mCallbackManager, mLoginCallback);

                //화면 새로고침
                tv_facebook.setText("페이스북 연동됨");
                tv_facebook_logout.setText("연동 해제");
                onResume();
            }
        });

        //로그아웃
        btn_facebook_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_facebook_logout.setEnabled(false);
                btn_facebook.setEnabled(true);

                LoginManager.getInstance().logOut();
                btn_facebook_logout.setEnabled(false);
                Toast.makeText(getApplicationContext(), "페이스북 연동이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                //화면 새로고침
                tv_facebook.setText("페이스북 연동하기");
                tv_facebook_logout.setText("");
                onResume();
            }
        });

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        btn_facebook_logout.setEnabled(true);
                        btn_facebook.setEnabled(false);

                        Log.v("result", object.toString());
                        Toast.makeText(getApplicationContext(), "페이스북 연동", Toast.LENGTH_SHORT).show();
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr",error.toString());
            }
        });


    }

    //callbackManager.onActivityResult를 호출하여 로그인 결과를 callbackManager를 통해 LoginManager에 전달
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

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



    public void shareFacebook(){
        ShareLinkContent content = new ShareLinkContent.Builder()
                //공유될 링크
                .setContentUrl(Uri.parse("fb1182541458751255://"))
                .setQuote("INSTABOOK의 리뷰를 만나보세요!")
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }

}
