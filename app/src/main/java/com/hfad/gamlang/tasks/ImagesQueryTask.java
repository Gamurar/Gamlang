package com.hfad.gamlang.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.hfad.gamlang.AddWordsFragment;
import com.hfad.gamlang.R;
import com.hfad.gamlang.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImagesQueryTask extends AsyncTask<Void, Void, HashMap<Integer, Bitmap>> {

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
        protected HashMap<Integer, Bitmap> doInBackground(Void...voids) {
                HashMap<Integer, Bitmap> images = new LinkedHashMap<>();
            try {
                String imagesJSON = NetworkUtils.getImagesJSON(
                        NetworkUtils.buildUrl(
                                AddWordsFragment.word.getName(),
                                NetworkUtils.IMAGE_SEARCH_ACTION));
                HashMap<Integer, String> imgsURL = NetworkUtils.getImagesURLFromJSON(imagesJSON);

                if (imgsURL != null && !imgsURL.isEmpty()) {
                    for (Map.Entry<Integer, String> entry : imgsURL.entrySet()) {
                        int id = entry.getKey();
                        String url = entry.getValue();
                        Bitmap bitmap = Picasso.get().load(url)
                                .get();

                        images.put(id, bitmap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return images;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, Bitmap> images) {
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
