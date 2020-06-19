package com.example.instabook.Activity.ForHashTag;

import android.content.Context;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class Hashtag extends ClickableSpan {
    Context context;
    TextPaint textPaint;


    public Hashtag(Context ctx) {
        super();
        context = ctx;
    }


    @Override
    public void updateDrawState(TextPaint ds) {
        textPaint = ds;
        ds.setColor(ds.linkColor);
        ds.setARGB(255, 30, 144, 255);
    }


    @Override
    public void onClick(@NonNull View widget) {
        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        String theWord = s.subSequence(start + 1, end).toString();
        // you can start another activity here
        Toast.makeText(context, String.format("Tag : %s", theWord), 10 ).show();

    }
}
