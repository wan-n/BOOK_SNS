package com.example.instabook.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.instabook.R;

public class PMIdDialog extends Dialog implements View.OnClickListener {
    private FrameLayout fr_cancel;
    private Context context;

    private PMIdDialogListener pmIdDialogListener;

    public PMIdDialog(Context context){
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface PMIdDialogListener{
        void onPositiveClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(PMIdDialogListener pmIdDialogListener){
        this.pmIdDialogListener = pmIdDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_pmid_dialog);

        fr_cancel = findViewById(R.id.fr_cancel);

        fr_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fr_cancel:
                pmIdDialogListener.onPositiveClicked();
                cancel();
                break;
        }
    }
}
