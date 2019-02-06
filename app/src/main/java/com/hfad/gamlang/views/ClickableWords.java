package com.hfad.gamlang.views;

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

import com.hfad.gamlang.utilities.WordClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import static android.graphics.Typeface.BOLD;

public class ClickableWords extends AppCompatTextView {
    private static final String TAG = "ClickableWords";
    private StyleSpan mBoldSpan = new StyleSpan(BOLD);
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
                    Log.d(TAG, "onClick: word clicked");
                    Toast.makeText(getContext(), "The word " + this.word + " clicked", Toast.LENGTH_LONG).show();
                }
            };
            spString.setSpan(clickSpan, start, end, 0);
        }
        super.setText(spString, BufferType.SPANNABLE);
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


//    private abstract class TouchableSpan extends ClickableSpan {
//        private boolean mIsPressed;
//        private int mPressedBackgroundColor;
//        private int mNormalTextColor;
//        private int mPressedTextColor;
//        public String word;
//
//
//        public TouchableSpan(String word) {
//            this.word = word;
//            mNormalTextColor = getResources().getColor(android.R.color.widget_edittext_dark);
//            mPressedTextColor = getResources().getColor(android.R.color.holo_orange_light);
//            mPressedBackgroundColor = getResources().getColor(android.R.color.background_light);
//        }
//
//        public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
//            mNormalTextColor = normalTextColor;
//            mPressedTextColor = pressedTextColor;
//            mPressedBackgroundColor = pressedBackgroundColor;
//        }
//
//        public void setPressed(boolean isSelected) {
//            mIsPressed = isSelected;
//        }
//
//        @Override
//        public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
//            ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
//            ds.bgColor = mIsPressed ? mPressedBackgroundColor : 0xffeeeeee;
//            ds.setUnderlineText(false);
//        }
//    }
//
//    private class LinkTouchMovementMethod extends LinkMovementMethod {
//        private TouchableSpan mPressedSpan;
//
//        @Override
//        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                mPressedSpan = getPressedSpan(textView, spannable, event);
//                if (mPressedSpan != null) {
//                    mPressedSpan.setPressed(true);
//                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
//                            spannable.getSpanEnd(mPressedSpan));
//                }
//            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
//                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
//                    mPressedSpan.setPressed(false);
//                    mPressedSpan = null;
//                    Selection.removeSelection(spannable);
//                }
//            } else {
//                if (mPressedSpan != null) {
//                    mPressedSpan.setPressed(false);
//                    super.onTouchEvent(textView, spannable, event);
//                }
//                mPressedSpan = null;
//                Selection.removeSelection(spannable);
//            }
//            return true;
//        }
//
//        private TouchableSpan getPressedSpan(
//                TextView textView,
//                Spannable spannable,
//                MotionEvent event) {
//
//            int x = (int) event.getX() - textView.getTotalPaddingLeft() + textView.getScrollX();
//            int y = (int) event.getY() - textView.getTotalPaddingTop() + textView.getScrollY();
//
//            Layout layout = textView.getLayout();
//            int position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x);
//
//            TouchableSpan[] link = spannable.getSpans(position, position, TouchableSpan.class);
//            TouchableSpan touchedSpan = null;
//            if (link.length > 0 && positionWithinTag(position, spannable, link[0])) {
//                touchedSpan = link[0];
//            }
//
//            return touchedSpan;
//        }
//
//        private boolean positionWithinTag(int position, Spannable spannable, Object tag) {
//            return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag);
//        }
//    }

    private class ClickableWord extends ClickableSpan {
        public String word;

        public ClickableWord(String word) {
            super();
            this.word = word;
        }

        @Override
        public void onClick(@NonNull View widget) {
            setTextColor(
                    getResources().getColor(android.R.color.holo_orange_light));
            widget.invalidate();
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(getResources().getColor(android.R.color.widget_edittext_dark));
        }

    }
}