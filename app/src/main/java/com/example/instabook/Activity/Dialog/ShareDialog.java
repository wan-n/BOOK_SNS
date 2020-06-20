package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.instabook.R;

public class ShareDialog extends Dialog implements View.OnClickListener {
    private FrameLayout fr_logout, fr_cancel;
    private TextView tv_title, tv_content, tv_logout;
    private Context context;

    private ShareDialogListener shareDialogListener;

    public ShareDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface ShareDialogListener{
        void onPositiveClicked();
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(ShareDialogListener shareDialogListener){
        this.shareDialogListener = shareDialogListener;
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
        tv_logout = findViewById(R.id.tv_logout);

        tv_title.setText("리뷰 공유");
        tv_content.setText("리뷰를 카카오톡으로 공유하시겠습니까?");
        tv_logout.setText("공유");


        //버튼 클릭 리스너 등록
        fr_logout.setOnClickListener(this);
        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_logout: //공유 버튼
                shareDialogListener.onPositiveClicked();
                dismiss();
                break;
            case R.id.fr_cancel: //취소 버튼을 눌렀을 때
                shareDialogListener.onNegativeClicked();
                cancel();
                break;
        }
    }
}
