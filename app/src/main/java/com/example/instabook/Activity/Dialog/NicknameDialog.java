package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.instabook.R;

public class NicknameDialog extends Dialog implements View.OnClickListener {
    private FrameLayout fr_change, fr_cancel;
    private EditText et_nickname;
    private Context context;

    private NicknameDialogListener nicknameDialogListener;

    public NicknameDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface NicknameDialogListener{
        void onPositiveClicked(String nickname);
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(NicknameDialogListener nicknameDialogListener){
        this.nicknameDialogListener = nicknameDialogListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_nickname_dialog);

        //init
        fr_change = findViewById(R.id.fr_change);
        fr_cancel = findViewById(R.id.fr_cancel);
        et_nickname = findViewById(R.id.et_nickname);


        //버튼 클릭 리스너 등록
        fr_change.setOnClickListener(this);
        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_change: //변경 버튼
                String nickname = et_nickname.getText().toString().trim();
                nicknameDialogListener.onPositiveClicked(nickname);
                dismiss();
                break;
            case R.id.fr_cancel: //취소 버튼을 눌렀을 때
                nicknameDialogListener.onNegativeClicked();
                cancel();
                break;
        }
    }
}
