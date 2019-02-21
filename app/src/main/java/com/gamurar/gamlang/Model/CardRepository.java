package com.gamurar.gamlang.Model;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gamurar.gamlang.View.ExploreActivity;
import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.Model.database.AppDatabase;
import com.gamurar.gamlang.Model.database.CardDao;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.View.ExploreFragment;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.AppExecutors;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.LiveSearchHelper;
import com.gamurar.gamlang.utilities.MySingleton;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.PreferencesUtils;
import com.gamurar.gamlang.utilities.ProgressableAdapter;
import com.gamurar.gamlang.utilities.Updatable;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ImageViewBitmap;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class CardRepository {
    private static final String TAG = "CardRepository";

    public static final String INSERT_TASK = "insert_task";
    public static final String DELETE_TASK = "delete_task";
    public static final String DELETE_ALL_TASK = "delete_all_task";

    public static File picturesDirectory;
    public static File musicDirectory;


    private CardDao cardDao;
    private LiveData<List<CardEntry>> mCardEntries;
    private LiveData<List<Card>> cards;
    public static String[] wikiOpenSearchWords;
    private Context mContext;
    private static String mFromLangCode;
    private static String mToLangCode;
    private static boolean mIsReversed = false;
    public static RequestQueue requestQueue;
    private static ProgressableAdapter mAdapter;

    public CardRepository(Context context) {
        mContext = context;
    }

    public void initLocal() {
        AppDatabase db = AppDatabase.getInstance(mContext);
        cardDao = db.cardDao();
        mCardEntries = cardDao.loadAllCards();
        cards = Transformations.map(mCardEntries, cardEntries -> {
            CardEntry[] entries = new CardEntry[cardEntries.size()];
            List<Card> cards = null;
            try {
                cards = new Tasks.createCardsAsyncTask()
                        .execute(cardEntries.toArray(entries))
                        .get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return cards;
        });

        File[] myDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        picturesDirectory = myDirs.length > 1 ? myDirs[1] : myDirs[0];

        myDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
        musicDirectory = myDirs.length > 1 ? myDirs[1] : myDirs[0];
    }

    public void initRemote() {
        requestQueue = MySingleton.getInstance(mContext).getRequestQueue();
        mFromLangCode = PreferencesUtils.getPrefFromLangCode(mContext);
        mToLangCode = PreferencesUtils.getPrefToLangCode(mContext);
    }

    public void initOpenSearch(ProgressableAdapter adapter) {
        mAdapter = adapter;
        Log.d(TAG, "initOpenSearch: Repository Adapter: " + mAdapter);
    }

    public LiveData<List<CardEntry>> getCardEntries() {
        return mCardEntries;
    }

    public LiveData<List<Card>> getAllCards() {
        return cards;
    }

    public void insert(CardEntry cardEntry) {
        new Tasks.databaseAsyncTask(cardDao, INSERT_TASK).execute(cardEntry);
    }

    public void delete(CardEntry cardEntry) {
        new Tasks.databaseAsyncTask(cardDao, DELETE_TASK).execute(cardEntry);
    }

    public void delete(HashSet<Card> cards) {
        Integer[] cardIds = new Integer[cards.size()];
        int i = 0;
        for (Card card : cards) {
            cardIds[i] = card.getId();
            i++;

            if (card.hasPictures()) {
                new Tasks.deletePicturesAsyncTask().execute(card.getPictureFileNames());
            }
            if (card.hasSound()) {
                new Tasks.deleteSoundAsyncTask().execute(card.getSoundFileName());
            }
        }
        new Tasks.databaseDeleteByIdAsyncTask(cardDao).execute(cardIds);
    }

    public void deleteById(Integer[] cardIds) {
        new Tasks.databaseDeleteByIdAsyncTask(cardDao).execute(cardIds);
    }

    public void deleteById(int cardId) {
        new Tasks.databaseDeleteByIdAsyncTask(cardDao).execute(cardId);
    }

    public void deleteAllCards() {
        new Tasks.databaseAsyncTask(cardDao, DELETE_ALL_TASK).execute();
    }

    public List<Card> getCards(List<CardEntry> cardEntries) {
        CardEntry[] entries = new CardEntry[cardEntries.size()];
        List<Card> cards = null;
        try {
            cards = new Tasks.createCardsAsyncTask()
                    .execute(cardEntries.toArray(entries))
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public void translateWord(ExploreActivity fragment, String word) {
        new Tasks.translateQueryTask(fragment, mFromLangCode, mToLangCode).execute(word);
        new Tasks.soundQueryAsyncTask(fragment).execute(word);
        new Tasks.imagesQueryTask(fragment, mFromLangCode).execute(word);
    }

    public void fetchImages(String word, ImagesLoadable imagesLoadable) {
        new Tasks.imagesQueryTask(imagesLoadable, mFromLangCode).execute(word);
    }

    public void translateWordOnly(WordTranslation fragment, String word) {
        new Tasks.translateQueryTask(fragment, mFromLangCode, mToLangCode).execute(word);
    }

    public String translateByGlosbe(String word) {
        if (isReversed())
            return NetworkUtils.translateByGlosbe(word, mToLangCode, mFromLangCode);
        else
            return NetworkUtils.translateByGlosbe(word, mFromLangCode, mToLangCode);
    }

    public String savePictures(HashSet<ImageViewBitmap> imageViews) {
        try {
            String fileNames = new Tasks.savePicturesAsyncTask()
                    .execute( imageViews.toArray(new ImageViewBitmap[imageViews.size()]) )
                    .get();

            return fileNames;

        } catch (ExecutionException | ConcurrentModificationException | InterruptedException e) {
            e.printStackTrace();

            return null;
        }
    }

    public void saveSound(String url) {
        new Tasks.savePronunciationAsyncTask().execute(url);
    }

    public String[] getOpenSearchLiveData() {
        return wikiOpenSearchWords;
    }

    public static void openSearch() {
//        AppExecutors.getInstance().networkIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                Tasks.loadSuggestionCards loadCardsTask = new Tasks.loadSuggestionCards(mAdapter, mFromLangCode, mToLangCode);
//                while (LiveSearchHelper.isTyping) {
//                    String query;
//                    while (LiveSearchHelper.lastTyped.isEmpty()) {
//
//                        this.wait();
//                    }
//                    LiveSearchHelper.lastSearched = query;
//                    String[] words = NetworkUtils.wikiOpenSearchRequest(query);
//                    if (LiveSearchHelper.lastTyped.equals(query)) {
//                        loadCardsTask.execute(words);
//                    }
//
//                }
//            }
//        });


//        String[] words;
//        Runnable wikiOpenSearch = new Runnable() {
//            @Override
//            public void run() {
//                String word = LiveSearchHelper.lastTyped;
//                LiveSearchHelper.lastSearched = word;
//                LiveSearchHelper.isSearching = true;
//                words = NetworkUtils.wikiOpenSearchRequest(word);
//            }
//        };
//
//        Runnable main = new Runnable() {
//            @Override
//            public void run() {
//                Executor executor = AsyncTask.SERIAL_EXECUTOR;
//                while (LiveSearchHelper.isTyping) {
//                    executor.execute(wikiOpenSearch);
//                }
//            }
//
//        };


    }

    public static void loadSuggestionCards(String[] words) {
        if (mIsReversed) {
            new Tasks.loadSuggestionCards(mAdapter, mToLangCode, mFromLangCode).execute(words);
        } else {
            new Tasks.loadSuggestionCards(mAdapter, mFromLangCode, mToLangCode).execute(words);
        }
    }

    public void reverseSearchLang() {
        mIsReversed = !mIsReversed;
    }

    public boolean isReversed() {
        return mIsReversed;
    }

    public void gatherWordInfo(Word word, Updatable updatable) {
        Log.d(TAG, "Word object: " + word);
        new Tasks.gatherWordInfo(mFromLangCode, mToLangCode, updatable).execute(word);
    }

    public void setReversed(boolean isReversed) {
        mIsReversed = isReversed;
    }

    public String getPrefFromLang() {
        return PreferencesUtils.getPrefFromLang(mContext);
    }

    public String getPrefToLang() {
        return PreferencesUtils.getPrefToLang(mContext);
    }

    private class OpenSearchRunnable implements Runnable {

        @Override
        public void run() {
            Tasks.loadSuggestionCards loadCardsTask = new Tasks.loadSuggestionCards(mAdapter, mFromLangCode, mToLangCode);
            while (LiveSearchHelper.isTyping) {
                String query = LiveSearchHelper.lastTyped;
                LiveSearchHelper.lastSearched = query;
                String[] words = NetworkUtils.wikiOpenSearchRequest(query);
                if (LiveSearchHelper.lastTyped.equals(query)) {
                    loadCardsTask.execute(words);
                }

            }
        }
    }
}


