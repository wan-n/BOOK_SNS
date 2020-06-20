package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.instabook.R;

public class EmailDialog extends Dialog implements View.OnClickListener {
    private FrameLayout fr_change, fr_cancel;
    private EditText et_nickname;
    private Context context;
    private TextView tv_title, tv_content, tv_change, tv_cancel;

    private EmailDialogListener emailDialogListener;

    public EmailDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface EmailDialogListener{
        void onPositiveClicked(String email);
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(EmailDialogListener emailDialogListener){
        this.emailDialogListener = emailDialogListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_nickname_dialog);

        //init
        fr_change = findViewById(R.id.fr_change);
        fr_cancel = findViewById(R.id.fr_cancel);
        et_nickname = findViewById(R.id.et_nickname);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);

        tv_title.setText("이메일 변경");
        tv_content.setText("변경할 이메일을 입력해주세요.");


        //버튼 클릭 리스너 등록
        fr_change.setOnClickListener(this);
        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_change: //변경 버튼
                String email = et_nickname.getText().toString().trim();
                emailDialogListener.onPositiveClicked(email);
                dismiss();
                break;
            case R.id.fr_cancel: //취소 버튼을 눌렀을 때
                emailDialogListener.onNegativeClicked();
                cancel();
                break;
        }
    }
}
