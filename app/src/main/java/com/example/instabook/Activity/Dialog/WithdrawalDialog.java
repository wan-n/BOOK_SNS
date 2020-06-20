package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.instabook.R;

public class WithdrawalDialog extends Dialog implements View.OnClickListener {

private FrameLayout fr_logout, fr_cancel;
private TextView tv_title, tv_content;
private Context context;

private WithdrawalDialogListener withdrawalDialogListener;

public WithdrawalDialog(Context context) {
        super(context);
        this.context = context;
        }

//인터페이스 설정
public interface WithdrawalDialogListener{
    void onPositiveClicked();
    void onNegativeClicked();
}

    //호출할 리스너 초기화
    public void setDialogListener(WithdrawalDialogListener withdrawalDialogListener){
        this.withdrawalDialogListener = withdrawalDialogListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_logout_dialog);

        //init
        fr_logout = findViewById(R.id.fr_logout);
        fr_cancel = findViewById(R.id.fr_cancel);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);

        tv_title.setText("회원탈퇴");
        tv_content.setText("계정을 비활성화 하시겠습니까?");


        //버튼 클릭 리스너 등록
        fr_logout.setOnClickListener(this);
        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_logout: //확인 버튼
                withdrawalDialogListener.onPositiveClicked();
                dismiss();
                break;
            case R.id.fr_cancel: //취소 버튼을 눌렀을 때
                withdrawalDialogListener.onNegativeClicked();
                cancel();
                break;
        }
    }
}