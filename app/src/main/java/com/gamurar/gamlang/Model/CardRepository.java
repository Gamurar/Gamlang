package com.gamurar.gamlang.Model;

import android.content.Context;
import android.os.Environment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gamurar.gamlang.View.AddWordsActivity;
import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.Model.database.AppDatabase;
import com.gamurar.gamlang.Model.database.CardDao;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.PreferencesUtils;
import com.gamurar.gamlang.utilities.ProgressableAdapter;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public static MutableLiveData<String[]> wikiOpenSearchWords;
    public static RequestQueue requestQueue;
    private Context mContext;
    private String mFromLangCode;
    private String mToLangCode;
    private boolean mIsReversed = false;

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
        wikiOpenSearchWords = new MutableLiveData<>();
        requestQueue = Volley.newRequestQueue(mContext);
        mFromLangCode = PreferencesUtils.getPrefFromLangCode(mContext);
        mToLangCode = PreferencesUtils.getPrefToLangCode(mContext);
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

    public void translateWord(AddWordsActivity fragment, String word) {
        new Tasks.translateQueryTask(fragment, mFromLangCode, mToLangCode).execute(word);
        new Tasks.soundQueryAsyncTask(fragment).execute(word);
        new Tasks.imagesQueryTask(fragment).execute(word);
    }

    public void translateWordOnly(WordTranslation fragment, String word) {
        new Tasks.translateQueryTask(fragment, mFromLangCode, mToLangCode).execute(word);
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

    public LiveData<String[]> getOpenSearchLiveData() {
        return wikiOpenSearchWords;
    }

    public void openSearch(String word) {
        NetworkUtils.wikiOpenSearchRequest(word);
    }

    public void loadSuggestionCards(String[] word, ProgressableAdapter adapter) {
        if (mIsReversed) {
            new Tasks.loadSuggestionCards(adapter, mToLangCode, mFromLangCode).execute(word);
        } else {
            new Tasks.loadSuggestionCards(adapter, mFromLangCode, mToLangCode).execute(word);
        }
    }

    public void reverseSearchLang() {
        mIsReversed = !mIsReversed;
    }
}


