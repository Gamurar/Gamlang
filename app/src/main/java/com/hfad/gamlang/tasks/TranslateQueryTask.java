package com.hfad.gamlang.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.hfad.gamlang.AddWordsFragment;
import com.hfad.gamlang.R;
import com.hfad.gamlang.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class TranslateQueryTask extends AsyncTask<URL, Void, String> {

    private static final String TAG = "TranslateQueryTask";

    private final AddWordsFragment addWordsFragment;
    private ProgressBar mLoadingIndicator;

    public TranslateQueryTask(AddWordsFragment addWordsFragment) {
        this.addWordsFragment = addWordsFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        addWordsFragment.loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(URL... params) {
//        String ABBYYResponse = null;
//        //if there is no token, get token
//        if (NetworkUtils.authABBYYToken == null) {
//            URL authUrl = NetworkUtils.buildUrl(null, NetworkUtils.ABBYY_AUTH);
//            try {
//                ABBYYResponse = NetworkUtils.getABBYYAuthToken(authUrl);
//                NetworkUtils.authABBYYToken
//                        = ABBYYResponse.replaceAll("[\"]", "");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        URL queryUrl = params[0];
//        try {
//            ABBYYResponse = NetworkUtils.getABBYYTranslation(queryUrl);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ABBYYResponse;
        return NetworkUtils.translateByGlosbe(
                AddWordsFragment.word.getName(),
                addWordsFragment.getContext());
    }

    @Override
    protected void onPostExecute(String translation) {
        addWordsFragment.loadingIndicator.setVisibility(View.INVISIBLE);
        if (translation != null && !translation.equals("")) {
            AddWordsFragment.word.setTranslation(translation);
            addWordsFragment.translationTextView.setText(translation);
            addWordsFragment.allowAddToDict();
            new ImagesQueryTask(addWordsFragment).execute(translation);
        } else {
            showErrorMessage();
            addWordsFragment.forbidAddToDict();
        }
    }

    private void showErrorMessage() {
        addWordsFragment.translationTextView.setText(R.string.error_no_translation);
    }

    public void translate() {
        URL url = NetworkUtils.buildUrl(AddWordsFragment.word.getName(), NetworkUtils.ABBYY_SHORT_TRANSLATE);
        this.execute(url);
    }

}
