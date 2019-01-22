package com.hfad.gamlang.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hfad.gamlang.AddWordsFragment;
import com.hfad.gamlang.R;
import com.hfad.gamlang.utilities.AppExecutors;
import com.hfad.gamlang.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

public class ImagesQueryTask extends AsyncTask<String, HashMap<String, Bitmap>, HashMap<String, Bitmap>> {

    private static final String TAG = "TranslateQueryTask";

    private final AddWordsFragment addWordsFragment;

    public ImagesQueryTask(AddWordsFragment addWordsFragment) {
        this.addWordsFragment = addWordsFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        addWordsFragment.imagesErrorMessage.setVisibility(TextView.INVISIBLE);
        addWordsFragment.imagesLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected HashMap<String, Bitmap> doInBackground(String... queryWord) {
        String word = queryWord[0];
        if (word == null && TextUtils.isEmpty(word)) {
            return null;
        } else {
            HashMap<String, Bitmap> images = new LinkedHashMap<>();
            Executor mainThread = AppExecutors.getInstance().mainThread();

            ArrayList<String> imgsURL = NetworkUtils.fetchRelatedImagesUrl(word, "com");
            if (imgsURL != null && !imgsURL.isEmpty()) {
                for (String url : imgsURL) {
                    if (url != null && !TextUtils.isEmpty(url)) {
                        try {
                            Bitmap bitmap = Picasso.get()
                                    .load(url)
                                    .resize(200, 150)
                                    .get();
                            String id = url.substring(url.lastIndexOf("tbn:") + 4);

                            images.put(id, bitmap);

                            //mainThread.execute(() -> onProgressUpdate(images));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return images;
        }
    }

    @Override
    protected void onProgressUpdate(HashMap<String, Bitmap>... values) {
        addWordsFragment.mAdapter.setImages(values[0]);
    }

    @Override
    protected void onPostExecute(HashMap<String, Bitmap> images) {
        addWordsFragment.imagesLoadingIndicator.setVisibility(View.INVISIBLE);
        if (images != null && !images.isEmpty()) {
            addWordsFragment.mAdapter.setImages(images);
            //allowAddToDict();
        } else {
            showErrorMessage();
            //forbidAddToDict();
        }
    }

    private void showErrorMessage() {
        addWordsFragment.imagesErrorMessage.setText(R.string.error_no_images);
        addWordsFragment.imagesErrorMessage.setVisibility(TextView.VISIBLE);
    }
}
