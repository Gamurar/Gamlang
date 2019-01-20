package com.hfad.gamlang.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


public class ImageViewBitmap extends AppCompatImageView {

    private Bitmap mBitmap;
    private int mId;


    public ImageViewBitmap(Context context) {
        super(context);
    }

    public ImageViewBitmap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewBitmap(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }
}
