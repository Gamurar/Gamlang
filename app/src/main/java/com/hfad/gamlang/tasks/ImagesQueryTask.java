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
import java.util.Random;

public class ImagesQueryTask extends AsyncTask<String, Void, HashMap<String, Bitmap>> {

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
    protected HashMap<String, Bitmap> doInBackground(String... word) {
        HashMap<String, Bitmap> images = new LinkedHashMap<>();
        try {
//                String imagesJSON = NetworkUtils.getImagesJSON(
//                        NetworkUtils.buildUrl(
//                                AddWordsFragment.word.getName(),
//                                NetworkUtils.IMAGE_SEARCH_ACTION));
//                HashMap<Integer, String> imgsURL = NetworkUtils.getImagesURLFromJSON(imagesJSON);

//                if (imgsURL != null && !imgsURL.isEmpty()) {
//                    for (Map.Entry<Integer, String> entry : imgsURL.entrySet()) {
//                        int id = entry.getKey();
//                        String url = entry.getValue();
//                        Bitmap bitmap = Picasso.get().load(url)
//                                .get();
//
//                        images.put(id, bitmap);
//                    }
//                }


            ArrayList<String> imgsURL = NetworkUtils.fetchRelatedImagesUrl(word[0], "com");
            if (imgsURL != null && !imgsURL.isEmpty()) {
                for (String url : imgsURL) {
                    Bitmap bitmap = Picasso.get().load(url).get();
                    //TODO: get the photo id from the url
                    String id = url.substring(url.lastIndexOf("tbn:") + 4);

                    images.put(id, bitmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
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
