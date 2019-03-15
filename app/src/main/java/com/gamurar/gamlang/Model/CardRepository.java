package com.gamurar.gamlang.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.android.volley.RequestQueue;
import com.gamurar.gamlang.Card;
import com.gamurar.gamlang.Model.database.CardEntry;
import com.gamurar.gamlang.Model.database.ImageDao;
import com.gamurar.gamlang.Model.database.IntermediateDao;
import com.gamurar.gamlang.Model.database.IntermediateEntry;
import com.gamurar.gamlang.Model.database.SoundDao;
import com.gamurar.gamlang.View.ExploreActivity;
import com.gamurar.gamlang.Model.database.AppDatabase;
import com.gamurar.gamlang.Model.database.CardDao;
import com.gamurar.gamlang.Word;
import com.gamurar.gamlang.utilities.CardsObserver;
import com.gamurar.gamlang.utilities.ImagesLoadable;
import com.gamurar.gamlang.utilities.NetworkUtils;
import com.gamurar.gamlang.utilities.PreferencesUtils;
import com.gamurar.gamlang.utilities.ProgressableAdapter;
import com.gamurar.gamlang.utilities.WordInfoLoader;
import com.gamurar.gamlang.utilities.WordTranslation;
import com.gamurar.gamlang.views.ImageViewBitmap;

import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class CardRepository {
    private static final String TAG = "CardRepository";

    private static final Object LOCK = new Object();
    private static CardRepository sInstance;

    public static final String INSERT_TASK = "insert_task";
    public static final String DELETE_TASK = "delete_task";
    public static final String DELETE_ALL_TASK = "delete_all_task";

    public static File picturesDirectory;
    public static File musicDirectory;


    private CardDao cardDao;
    private ImageDao imageDao;
    private SoundDao soundDao;
    private IntermediateDao intermediateDao;
    private LiveData<List<IntermediateEntry>> mCardsData;
    private List<CardEntry> mCardEntries;
    private LiveData<List<Card>> mCards;
    private Context mContext;
    private static String mFromLangCode;
    private static String mToLangCode;
    private static boolean mIsReversed = false;
    private static ProgressableAdapter mAdapter;
    private static CardsObserver mCardsObserver;

    public static CardRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new repository instance");
                sInstance = new CardRepository(context);
                mCardsObserver = new CardsObserver();
            }
        }
        Log.d(TAG, "Getting the repository instance");
        return sInstance;
    }

    public CardRepository(Context context) {
        mContext = context;
    }

    public void initLocal() {
        if (cardDao != null) return;
        AppDatabase db = AppDatabase.getInstance(mContext);
        cardDao = db.cardDao();
        imageDao = db.imageDao();
        soundDao = db.soundDao();
        intermediateDao = db.intermediateDao();
        mCardsData = intermediateDao.loadAllCardsData();
        mCards = transformEntriesToCardsLiveData();
        mCards.observeForever(mCardsObserver);
        File[] myDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_PICTURES);
        picturesDirectory = myDirs.length > 1 ? myDirs[1] : myDirs[0];

        myDirs = mContext.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
        musicDirectory = myDirs.length > 1 ? myDirs[1] : myDirs[0];
    }

    private LiveData<List<Card>> transformEntriesToCardsLiveData() {
        return Transformations.map(mCardsData, cardsData -> {
            List<Card> cards = null;
            try {
                List<CardEntry> cardEntries = loadAllCards();
                if (cardEntries != null) {
                    CardEntry[] cardEntriesArr = cardEntries.toArray(new CardEntry[0]);
                    cards = new Tasks.createCardsAsyncTask(cardsData)
                            .execute(cardEntriesArr)
                            .get();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return cards;
        });
    }

    public void initRemote() {
        mFromLangCode = PreferencesUtils.getPrefFromLangCode(mContext);
        mToLangCode = PreferencesUtils.getPrefToLangCode(mContext);
    }

    public void initOpenSearch(ProgressableAdapter adapter) {
        mAdapter = adapter;
        Log.d(TAG, "initOpenSearch: Repository Adapter: " + mAdapter);
    }

    public LiveData<List<Card>> getLiveCards() {
        return mCards;
    }


    public void insertCard(CardEntry cardEntry, String[] images, String sound) {
        new Tasks.databaseAsyncTask(cardDao, INSERT_TASK,
                images, imageDao,
                sound, soundDao,
                intermediateDao).execute(cardEntry);
    }

    public void deleteCard(CardEntry cardEntry) {
        new Tasks.databaseAsyncTask(cardDao, DELETE_TASK,
                null, null, null, null,
                intermediateDao).execute(cardEntry);
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
                null, null, null, null,
                intermediateDao).execute();
    }

    public List<CardEntry> loadAllCards() {
        try {
            return new Tasks.dbLoadAllCards(cardDao).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateCardReview(int cardId, Date lastReview, Date nextReview) {
        new Tasks.dbUpdateReview(cardId, lastReview, nextReview, cardDao).execute();
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

    public String[] savePictures(HashSet<Pair<String, Bitmap>> images) {
        try {
            return new Tasks.savePicturesToStorage(images)
                    .execute()
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

    public void gatherWordInfo(Word word, WordInfoLoader updatable) {
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

    public String translateByWiki(String word) {
        return NetworkUtils.wikiTranslate(word);
    }

    public String translateByGamurar(String word) {
        return NetworkUtils.gamurarTranslate(word);
    }
}


