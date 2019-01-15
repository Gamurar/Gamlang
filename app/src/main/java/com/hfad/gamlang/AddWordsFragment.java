package com.hfad.gamlang;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.database.CardEntry;
import com.hfad.gamlang.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddWordsFragment extends Fragment {

    private static final String TAG = "AddWordsFragment";

    private static ArrayList<String> dict =
            new ArrayList<>();
    private static Word word = new Word("way");

    private TextView wordTextView;
    private TextView translationTextView;
    private ImageView playSoundImageView;
    private RecyclerView wordPictureRecyclerView;
    private ProgressBar loadingIndicator;
    private Button addToDictBtn;

    static ArrayList<String> imgsURL;

    private AppDatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //init views
        wordTextView = view.findViewById(R.id.tv_word);
        translationTextView = view.findViewById(R.id.tv_translation);
        loadingIndicator = view.findViewById(R.id.pb_loading_indicator);
        playSoundImageView = view.findViewById(R.id.iv_play);
        wordPictureRecyclerView = view.findViewById(R.id.rv_word_pictures);
        addToDictBtn = view.findViewById(R.id.btn_add_to_dict);
        word.setName("way");

        if (getArguments() != null) {
            CharSequence text = getArguments().getCharSequence(Intent.EXTRA_PROCESS_TEXT);
            if (text != null && !TextUtils.isEmpty(text)) {
                word.setName(text.toString());
            }
        }
        wordTextView.setText(word.getName());

        playSoundImageView.setOnClickListener(soundBtn -> word.playPronunc());

        addToDictBtn.setOnClickListener(btn -> {
            final CardEntry newCard = new CardEntry(word.name, word.translations.get(0).transVariants.get(0));
            final Toast toast = Toast.makeText(getContext(), "The word " + word.getName() + " added to the dictionary.", Toast.LENGTH_SHORT);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.cardDao().insertCard(newCard);
                    Log.d(TAG, "The word " + newCard.getWord() + " has been inserted to the Database");
                    toast.show();
                }
            });
        });
        //

        ABBYYTranslate();

        //ImagesAdapter adapter = new ImagesAdapter(imgsURL);
        wordPictureRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 3)
        );
        //wordPictureRecyclerView.setAdapter(adapter);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        super.onViewCreated(view, savedInstanceState);
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
//                String imagesJSON = NetworkUtils.getImagesJSON(
//                        NetworkUtils.buildUrl(
//                                word.getName(),
//                                NetworkUtils.IMAGE_SEARCH_ACTION));
//                imgsURL = NetworkUtils.getImagesURLFromJSON(imagesJSON);
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

//                ImagesAdapter adapter = new ImagesAdapter(imgsURL);
//                wordPictureRecyclerView.setAdapter(adapter);
            } else {
                // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
                //showErrorMessage();
            }
        }
    }

    public void ABBYYTranslate() {
        URL url = NetworkUtils.buildUrl(word.getName(), NetworkUtils.ABBYY_SHORT_TRANSLATE);
        new ABBYYQueryTask().execute(url);
    }
}
