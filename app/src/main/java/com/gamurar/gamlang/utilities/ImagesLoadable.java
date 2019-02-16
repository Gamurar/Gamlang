package com.gamurar.gamlang.utilities;

import android.graphics.Bitmap;
import android.util.Pair;

public interface ImagesLoadable {
    void addImage(Pair<String, Bitmap> image);
    void onLoadImagesStart();
    void onLoadImagesFinished();
    void showImagesErrorMessage();
}
