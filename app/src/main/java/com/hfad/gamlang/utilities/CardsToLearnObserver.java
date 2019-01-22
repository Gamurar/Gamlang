package com.hfad.gamlang.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.hfad.gamlang.Card;
import com.hfad.gamlang.database.CardEntry;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Observer;

public class CardsToLearnObserver implements Observer<List<CardEntry>> {
    private static final String TAG = "CardsToLearnObserver";

    private StorageHelper storageHelper;
    private ArrayList<Card> cards;

    public CardsToLearnObserver(Context context) {
        storageHelper = new StorageHelper(context);
    }

    @Override
    public void onChanged(List<CardEntry> cardEntries) {
        if (cardEntries.isEmpty()) {
            Log.d(TAG, "There is no cards retrieved from the DataBase");
            return;
        }

        //Bitmap[] pictures = storageHelper.getImages(cardEntries);

//        cards = new ArrayList<>();
//        for (CardEntry entry : cardEntries) {
//            Card card = new Card(entry.getWord(), entry.getTranslation());
//
//        }
    }
}
