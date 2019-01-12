package com.hfad.gamlang;

import android.app.Application;
import android.util.Log;

import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.database.CardEntry;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class LearnWordsViewModel extends AndroidViewModel {
    private static final String TAG = "LearnWordsViewModel";

    private LiveData<List<CardEntry>> cards;
    private static int mCurrentCardId = 0;
    private int cardsCount;

    public LearnWordsViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the cards from the DataBase");
        cards = db.cardDao().loadAllCards();
        if (cards != null) {
            Log.d(TAG, "Cards have been retrieved from the DataBase");
        } else {
            Log.d(TAG, "Retrieved null object from the DataBase");
        }
    }

    public LiveData<List<CardEntry>> getCards() {
        return cards;
    }
}
