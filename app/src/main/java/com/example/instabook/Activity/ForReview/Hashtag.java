package com.example.instabook.Activity.ForReview;

import android.content.Context;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class Hashtag extends ClickableSpan {
    //각각의 hash tag마다 클릭 가능

    public interface ClickEventListener{
        void onClickEvent(String data);
    }

    private  ClickEventListener mClickEventListener = null;

    private Context context;
    private TextPaint textPaint;

    public Hashtag(Context ctx) {
        super();
        context = ctx;
    }

    public void setOnClickEventListener(ClickEventListener listener){
        mClickEventListener = listener;
    } //listener로 클릭한 단어를 보내줌

    public void updateDrawState(TextPaint ds) {
        textPaint = ds;
        ds.setColor(ds.linkColor);
        ds.setARGB(255,30,144,255);
    }

    @Override
    public void onClick(@NonNull View widget) {
        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        String theWord = s.subSequence(start + 1, end).toString();
        mClickEventListener.onClickEvent(theWord);
    }
}
