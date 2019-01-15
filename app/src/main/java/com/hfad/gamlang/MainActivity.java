package com.hfad.gamlang;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.database.CardEntry;
import com.hfad.gamlang.utilities.ImagesAdapter;
import com.hfad.gamlang.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static ArrayList<String> dict =
            new ArrayList<>();
    private static Word word = new Word("way");

    private TextView wordTextView;
    private TextView translationTextView;
    private ImageView playSoundImageView;
    private RecyclerView wordPictureRecyclerView;
    private ProgressBar loadingIndicator;
    private DrawerLayout drawer;
    static ArrayList<String> imgsURL;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        ABBYYTranslate();

        //ImagesAdapter adapter = new ImagesAdapter(imgsURL);
        wordPictureRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 3)
        );
        //wordPictureRecyclerView.setAdapter(adapter);

        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_learn_words: {

                break;
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void addToDict(View btn) {
        final CardEntry newCard = new CardEntry(word.name, word.translations.get(0).transVariants.get(0));
        final Toast toast = Toast.makeText(this, "The word " + word.getName() + " added to the dictionary.", Toast.LENGTH_SHORT);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.cardDao().insertCard(newCard);
                Log.d(TAG, "The word " + newCard.getWord() + " has been inserted to the Database");
                toast.show();
            }
        });

    }

    public void ABBYYTranslate() {
        URL url = NetworkUtils.buildUrl(word.getName(), NetworkUtils.ABBYY_SHORT_TRANSLATE);
        new ABBYYQueryTask().execute(url);
    }

    public void onClickLearnWords(View view) {
        Intent intent = new Intent(this, LearnWordsActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        wordTextView = findViewById(R.id.tv_word);
        translationTextView = findViewById(R.id.tv_translation);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);
        playSoundImageView = findViewById(R.id.iv_play);
        wordPictureRecyclerView = findViewById(R.id.rv_word_pictures);

        CharSequence text = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        if (text != null) {
            word.setName(text.toString());
            wordTextView.setText(word.getName());
        }

        //navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        playSoundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                word.playPronunc();
            }
        });
    }
}
