package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.instabook.R;

public class ProfileDialog extends Dialog implements View.OnClickListener {

    private FrameLayout fr_basic, fr_album, fr_cancel;
    private TextView tv_basic, tv_album, tv_cancel;

    private Context context;

    private ProfileDialogListener profileDialogListener;

    public ProfileDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface ProfileDialogListener{
        void onPositiveClicked();
        void onNegativeClicked();
        void onNeutralClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(ProfileDialogListener profileDialogListener){
        this.profileDialogListener = profileDialogListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_profile_dialog);

        //init
        fr_basic = findViewById(R.id.fr_basic);
        fr_album = findViewById(R.id.fr_album);
        fr_cancel = findViewById(R.id.fr_cancel);
        tv_basic = findViewById(R.id.tv_basic);
        tv_album = findViewById(R.id.tv_album);
        tv_cancel = findViewById(R.id.tv_cancel);

        //버튼 클릭 리스너 등록
        fr_basic.setOnClickListener(this);
        fr_album.setOnClickListener(this);
        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_album: //앨범 버튼
                profileDialogListener.onPositiveClicked();
                dismiss();
                break;
            case R.id.fr_basic: //기본 버튼
                profileDialogListener.onNeutralClicked();
                dismiss();
                break;
            case R.id.fr_cancel: //취소 버튼을 눌렀을 때
                profileDialogListener.onNegativeClicked();
                cancel();
                break;
        }
    }

}
