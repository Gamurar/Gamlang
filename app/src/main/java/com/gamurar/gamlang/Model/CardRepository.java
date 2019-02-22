package com.gamurar.gamlang.Model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.Model.database.ImageDao;
import com.gamurar.gamlang.Model.database.ImageEntry;
import com.gamurar.gamlang.Model.database.SoundDao;
import com.gamurar.gamlang.View.ExploreActivity;
import com.gamurar.gamlang.Model.database.AppDatabase;
import com.gamurar.gamlang.Model.database.CardDao;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.LiveSearchHelper;
import com.gamurar.gamlang.utilities.MySingleton;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.PreferencesUtils;
import com.gamurar.gamlang.utilities.ProgressableAdapter;
import com.gamurar.gamlang.utilities.Updatable;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class CardRepository {
    private static final String TAG = "CardRepository";

    public static final String INSERT_TASK = "insert_task";
    public static final String DELETE_TASK = "delete_task";
    public static final String DELETE_ALL_TASK = "delete_all_task";

    public static File picturesDirectory;
    public static File musicDirectory;


    private CardDao cardDao;
    private ImageDao imageDao;
    private SoundDao soundDao;
    private LiveData<List<CardEntry>> mCardEntries;
    private LiveData<List<com.gamurar.gamlang.Card>> cards;
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
        imageDao = db.imageDao();
        soundDao = db.soundDao();
        mCardEntries = cardDao.loadAllCards();
        cards = Transformations.map(mCardEntries, cardEntries -> {
            CardEntry[] entries = new CardEntry[cardEntries.size()];
            List<com.gamurar.gamlang.Card> cards = null;
            try {
                cards = new Tasks.createCardsAsyncTask(cardDao, imageDao, soundDao)
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

    public LiveData<List<com.gamurar.gamlang.Card>> getAllCards() {
        return cards;
    }



    public void insertCard(CardEntry cardEntry, String[] images, String sound) {
        new Tasks.databaseAsyncTask(cardDao, INSERT_TASK,
                images, imageDao,
                sound, soundDao).execute(cardEntry);
    }

    public void deleteCard(CardEntry cardEntry) {
        new Tasks.databaseAsyncTask(cardDao, DELETE_TASK,
                null, null, null, null).execute(cardEntry);
    }

    public void deleteCard(HashSet<Card> cards) {
        Integer[] cardIds = new Integer[cards.size()];
        int i = 0;
        for (com.gamurar.gamlang.Card card : cards) {
            cardIds[i] = card.getId();
            i++;

            if (card.hasPictures()) {
                new Tasks.deletePicturesAsyncTask(imageDao).execute(card.getPictureFileNames());
            }
            if (card.hasSound()) {
                new Tasks.deleteSoundAsyncTask(soundDao).execute(card.getSoundFileName());
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
        new Tasks.databaseAsyncTask(cardDao, DELETE_ALL_TASK,
                null, null, null, null).execute();
    }

    public List<Card> getCards(List<CardEntry> cardEntries) {
        CardEntry[] entries = new CardEntry[cardEntries.size()];
        List<com.gamurar.gamlang.Card> cards = null;
        try {
            cards = new Tasks.createCardsAsyncTask(cardDao, imageDao, soundDao)
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

    public String[] savePictures(HashSet<ImageViewBitmap> imageViews) {
        try {
            return new Tasks.savePicturesAsyncTask(imageDao)
                    .execute( imageViews.toArray(new ImageViewBitmap[imageViews.size()]) )
                    .get();

        } catch (ExecutionException | ConcurrentModificationException | InterruptedException e) {
            e.printStackTrace();

            return null;
        }
    }

    public String saveSound(String url) {
        try {
            return new Tasks.savePronunciationAsyncTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static void openSearch() {

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
}


