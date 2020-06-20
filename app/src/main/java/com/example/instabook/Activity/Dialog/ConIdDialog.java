package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.instabook.R;

public class ConIdDialog extends Dialog implements View.OnClickListener {
    private FrameLayout fr_cancel;
    private Context context;

    private ConIdDialogListener conIdDialogListener;

    public ConIdDialog(Context context){
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface ConIdDialogListener{
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(ConIdDialogListener conIdDialogListener){
        this.conIdDialogListener = conIdDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_conid_dialog);

        fr_cancel = findViewById(R.id.fr_cancel);

        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_cancel:
                conIdDialogListener.onNegativeClicked();
                cancel();
                break;
        }
    }
}
