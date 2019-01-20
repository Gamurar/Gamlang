package com.hfad.gamlang.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.hfad.gamlang.AddWordsFragment;
import com.hfad.gamlang.R;
import com.hfad.gamlang.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ImagesQueryTask extends AsyncTask<URL, Void, ArrayList<String>> {

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
        protected ArrayList<String> doInBackground(URL... params) {
            ArrayList<String> imgsURL = null;
            try {
                String imagesJSON = NetworkUtils.getImagesJSON(
                        NetworkUtils.buildUrl(
                                AddWordsFragment.word.getName(),
                                NetworkUtils.IMAGE_SEARCH_ACTION));
                imgsURL = NetworkUtils.getImagesURLFromJSON(imagesJSON);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imgsURL;
        }

        @Override
        protected void onPostExecute(ArrayList<String> imgsURL) {
            addWordsFragment.imagesLoadingIndicator.setVisibility(View.INVISIBLE);
            if (imgsURL != null && !imgsURL.isEmpty()) {
                addWordsFragment.mAdapter.setImages(imgsURL);
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

    public void fetchImages() {
        URL url = NetworkUtils.buildUrl(AddWordsFragment.word.getName(), NetworkUtils.ABBYY_SHORT_TRANSLATE);
        this.execute(url);
    }

}
