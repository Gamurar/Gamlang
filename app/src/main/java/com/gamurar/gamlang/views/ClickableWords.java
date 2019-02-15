package com.gamurar.gamlang.views;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gamurar.gamlang.utilities.WordClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import static android.graphics.Typeface.BOLD;

public class ClickableWords extends AppCompatTextView {
    private static final String TAG = "ClickableWords";
    private StyleSpan mBoldSpan = new StyleSpan(BOLD);
    private int mColor = -1;
    private WordClick mWordClick;

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
        setHighlightColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mWordClick != null) {
            setText(text, mWordClick);
            return;
        }
        super.setText(text, type);
    }

    public void setText(CharSequence text, WordClick wordClick) {
        Pattern pattern = Pattern
                .compile("(\\w+)"
                        , Pattern.CASE_INSENSITIVE);
        SpannableString spString = new SpannableString(text);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ClickableWord clickSpan = new ClickableWord(matcher.group()) {
                @Override
                public void onClick(@NonNull View widget) {
                    if (spString.getSpanStart(mBoldSpan) == -1) {
                        selectWord(spString, start, end);
                    } else {
                        spString.removeSpan(mBoldSpan);
                        selectWord(spString, start, end);
                    }

                    wordClick.onClick(this.word);

                }

                private void selectWord(SpannableString spString, int start, int end) {
                    spString.setSpan(mBoldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setText(spString, BufferType.SPANNABLE);
                }
            };
            spString.setSpan(clickSpan, start, end, 0);
        }

        super.setText(spString, BufferType.SPANNABLE);
    }

    public void setTextColorWhite() {
        mColor = 17170443;
    }

    public void setWordClickCallback(WordClick wordClick) {
        mWordClick = wordClick;
    }

    private class ClickableWord extends ClickableSpan {
        public String word;

        public ClickableWord(String word) {
            super();
            this.word = word;
        }

        @Override
        public void onClick(@NonNull View widget) {
            widget.invalidate();
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            if (mColor != -1) {
                ds.setColor(getResources().getColor(android.R.color.white));
            } else {
                ds.setColor(getResources().getColor(android.R.color.widget_edittext_dark));
            }
        }

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}