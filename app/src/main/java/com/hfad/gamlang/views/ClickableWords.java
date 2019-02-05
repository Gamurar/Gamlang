package com.hfad.gamlang.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import androidx.appcompat.widget.AppCompatTextView;

public class ClickableWords extends AppCompatTextView {
    public ClickableWords(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public ClickableWords(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ClickableWords(Context context) {
        super(context);
        init();
    }
    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        //define the matchin pattern
        Pattern pattern = Pattern
                .compile("(\\w+)"
                        , Pattern.CASE_INSENSITIVE);
        //build a spannable String using the Consequence
        SpannableString spString = new SpannableString(text);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "You jus clicked on a Bible link", Toast.LENGTH_LONG).show();
                }
            };
            spString.setSpan(clickSpan, start, end, 0);
        }
        super.setText(spString, BufferType.SPANNABLE);
    }
}