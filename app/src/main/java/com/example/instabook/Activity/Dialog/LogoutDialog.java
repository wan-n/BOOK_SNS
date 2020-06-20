package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.instabook.R;

public class LogoutDialog extends Dialog implements View.OnClickListener {

    private FrameLayout fr_logout, fr_cancel;
    private Context context;

    private LogoutDialogListener logoutDialogListener;

    public LogoutDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface LogoutDialogListener{
        void onPositiveClicked();
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(LogoutDialogListener logoutDialogListener){
        this.logoutDialogListener = logoutDialogListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_logout_dialog);

        //init
        fr_logout = findViewById(R.id.fr_logout);
        fr_cancel = findViewById(R.id.fr_cancel);


        //버튼 클릭 리스너 등록
        fr_logout.setOnClickListener(this);
        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_logout: //확인 버튼
                logoutDialogListener.onPositiveClicked();
                dismiss();
                break;
            case R.id.fr_cancel: //취소 버튼을 눌렀을 때
                logoutDialogListener.onNegativeClicked();
                cancel();
                break;
        }
    }
}
