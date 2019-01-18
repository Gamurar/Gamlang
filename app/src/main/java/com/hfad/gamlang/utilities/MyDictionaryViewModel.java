package com.hfad.gamlang.utilities;

import com.hfad.gamlang.database.AppDatabase;
import com.hfad.gamlang.database.CardEntry;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MyDictionaryViewModel extends ViewModel {

    private LiveData<List<CardEntry>> mCards;
    private AppDatabase mDb;

    public MyDictionaryViewModel(AppDatabase db) {
        mDb = db;
        mCards = db.cardDao().loadAllCards();
    }

    public LiveData<List<CardEntry>> getCards() {
        return mCards;
    }

    public void deleteCardsById(Integer[] cardsId) {
        mDb.cardDao().deleteCards(cardsId);
    }
}
