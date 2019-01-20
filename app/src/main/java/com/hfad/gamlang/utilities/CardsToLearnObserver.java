//package com.hfad.gamlang.utilities;
//
//import android.util.Log;
//
//import com.hfad.gamlang.Card;
//import com.hfad.gamlang.database.CardEntry;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import androidx.lifecycle.Observer;
//
//public class CardsToLearnObserver implements Observer<List<CardEntry>> {
//    private static final String TAG = "CardsToLearnObserver";
//
//    @Override
//    public void onChanged(List<CardEntry> cardEntries) {
//        if (cardEntries.isEmpty()) {
//            Log.d(TAG, "There is no cards retrieved from the DataBase");
//            return;
//        }
//
//        mCards = new ArrayList<>();
//        for (CardEntry entry : cardEntries) {
//            Card card = new Card(entry.getWord(), entry.getTranslation());
//            storageHelper.getImages(entry.getImage());
//        }
//        mCurrentCardId = 0;
//        Log.d(TAG, "Card set to review was updated");
//        mCurrentWord = mCardEntries.get(mCurrentCardId);
//        mQuestion.setText(mCurrentWord.getWord());
//        mAnswer.setText(mCurrentWord.getTranslation());
//        mCardCount = mCardEntries.size();
//        Log.d(TAG, "Card count: " + mCardCount);
//    }
//}
