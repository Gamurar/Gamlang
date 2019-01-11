package com.hfad.selectionmenu;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.selectionmenu.utilities.ImagesAdapter;
import com.hfad.selectionmenu.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<String> dict =
            new ArrayList<>();
    private static Word word = new Word("way");

    private TextView wordTextView;
    private TextView translationTextView;
    private ImageView playSoundImageView;
    private RecyclerView wordPictureRecyclerView;
    private ProgressBar loadingIndicator;
    static ArrayList<String> imgsURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordTextView = findViewById(R.id.tv_word);
        translationTextView = findViewById(R.id.tv_translation);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);

        CharSequence text = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        if (text != null) {
            word.setName(text.toString());
            wordTextView.setText(word.getName());
        }

        ABBYYTranslate();

        playSoundImageView = findViewById(R.id.iv_play);
        playSoundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                word.playPronunc();
            }
        });

        wordPictureRecyclerView = findViewById(R.id.rv_word_pictures);
        //ImagesAdapter adapter = new ImagesAdapter(imgsURL);
        wordPictureRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 3)
        );
        //wordPictureRecyclerView.setAdapter(adapter);
    }

    public class ABBYYQueryTask extends AsyncTask<URL, Void, String> {

        private static final String TAG = "ABBYYQueryTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            String ABBYYResponse = null;
            //if there is no token, get token
            if (NetworkUtils.authABBYYToken == null) {
                URL authUrl = NetworkUtils.buildUrl(null, NetworkUtils.ABBYY_AUTH);
                try {
                    ABBYYResponse = NetworkUtils.getABBYYAuthToken(authUrl);
                    NetworkUtils.authABBYYToken
                            = ABBYYResponse.replaceAll("[\"]", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            URL queryUrl = params[0];
            try {
                ABBYYResponse = NetworkUtils.getABBYYTranslation(queryUrl);
                String imagesJSON = NetworkUtils.getImagesJSON(
                        NetworkUtils.buildUrl(
                                word.getName(),
                                NetworkUtils.IMAGE_SEARCH_ACTION));
                imgsURL = NetworkUtils.getImagesURLFromJSON(imagesJSON);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ABBYYResponse;
        }

        @Override
        protected void onPostExecute(String ABBYYResponse) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (ABBYYResponse != null && !ABBYYResponse.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                //showJsonDataView();
                word = NetworkUtils.getWordFromShortTranslation(ABBYYResponse);
                translationTextView.setText(word.getTranslation());

                ImagesAdapter adapter = new ImagesAdapter(imgsURL);
                wordPictureRecyclerView.setAdapter(adapter);
            } else {
                // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
                //showErrorMessage();
            }
        }
    }

    public void addToDict(View btn) {
        Toast.makeText(this, "The word " + word + " added to the dictionary.", Toast.LENGTH_SHORT).show();

    }

    public void ABBYYTranslate() {
        URL url = NetworkUtils.buildUrl(word.getName(), NetworkUtils.ABBYY_SHORT_TRANSLATE);
        new ABBYYQueryTask().execute(url);
    }


}
